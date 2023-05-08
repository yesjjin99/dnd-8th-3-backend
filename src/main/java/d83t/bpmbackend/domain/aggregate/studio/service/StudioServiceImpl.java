package d83t.bpmbackend.domain.aggregate.studio.service;

import d83t.bpmbackend.domain.aggregate.studio.dto.StudioRequestDto;
import d83t.bpmbackend.domain.aggregate.studio.dto.StudioResponseDto;
import d83t.bpmbackend.domain.aggregate.studio.entity.Studio;
import d83t.bpmbackend.domain.aggregate.studio.repository.ScrapRepository;
import d83t.bpmbackend.domain.aggregate.studio.repository.StudioRepository;
import d83t.bpmbackend.domain.aggregate.user.entity.User;
import d83t.bpmbackend.domain.aggregate.user.repository.UserRepository;
import d83t.bpmbackend.exception.CustomException;
import d83t.bpmbackend.exception.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudioServiceImpl implements StudioService {

    private final StudioRepository studioRepository;
    private final ScrapRepository scrapRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public StudioResponseDto createStudio(StudioRequestDto requestDto) {
        Studio studio = requestDto.toEntity();
        studio.addRecommend(requestDto.getRecommends());

        Studio savedStudio = studioRepository.save(studio);

        return new StudioResponseDto(savedStudio, false);
    }

    @Override
    public StudioResponseDto findById(Long studioId, User user) {
        Studio studio = studioRepository.findById(studioId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_STUDIO));

        boolean isScrapped = checkStudioScrapped(studioId, user);
        return new StudioResponseDto(studio, isScrapped);
    }

    @Override
    public List<StudioResponseDto> findStudioAll(Integer limit, Integer offset, User user) {
        int pageSize = limit == null ? 20 : limit;
        int pageNumber = offset == null ? 0 : offset;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        User findUser = userRepository.findByKakaoId(user.getKakaoId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER_ID));
        List<Studio> studios = studioRepository.findByAll(pageable);

        return studios.stream().map(studio -> {
            boolean isScrapped = checkStudioScrapped(studio.getId(), findUser);
            return new StudioResponseDto(studio, isScrapped);
        }).collect(Collectors.toList());
    }

    @Override
    public List<StudioResponseDto> searchStudio(String q, User user) {
        List<Studio> studios = studioRepository.searchStudioNames(q);
        if (studios.isEmpty()) {
            throw new CustomException(Error.NOT_FOUND_STUDIO);
        }
        User findUser = userRepository.findByKakaoId(user.getKakaoId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER_ID));

        return studios.stream().map(studio -> {
            boolean isScrapped = checkStudioScrapped(studio.getId(), findUser);
            return new StudioResponseDto(studio, isScrapped);
        }).collect(Collectors.toList());
    }

    private boolean checkStudioScrapped(Long studioId, User user) {
        boolean isScrapped = false;
        if (scrapRepository.existsByStudioIdAndUserId(studioId, user.getId())) {
            isScrapped = true;
        }
        return isScrapped;
    }
}
