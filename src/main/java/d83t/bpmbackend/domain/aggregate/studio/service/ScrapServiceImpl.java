package d83t.bpmbackend.domain.aggregate.studio.service;

import d83t.bpmbackend.domain.aggregate.studio.entity.Scrap;
import d83t.bpmbackend.domain.aggregate.studio.entity.Studio;
import d83t.bpmbackend.domain.aggregate.studio.repository.ScrapRepository;
import d83t.bpmbackend.domain.aggregate.studio.repository.StudioRepository;
import d83t.bpmbackend.domain.aggregate.user.entity.User;
import d83t.bpmbackend.domain.aggregate.user.repository.UserRepository;
import d83t.bpmbackend.exception.CustomException;
import d83t.bpmbackend.exception.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ScrapServiceImpl implements ScrapService {

    private final ScrapRepository scrapRepository;
    private final StudioRepository studioRepository;
    private final UserRepository userRepository;

    @Override
    public void createScrap(Long studioId, User user) {
        User findUser = userRepository.findByKakaoId(user.getKakaoId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER_ID));
        Studio studio = studioRepository.findById(studioId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_STUDIO));

        Scrap scrap = Scrap.builder()
                .studio(studio)
                .user(findUser)
                .build();

        studio.addScrap(scrap);
        studioRepository.save(studio);
    }

    @Override
    public void deleteScrap(Long studioId, User user) {
        User findUser = userRepository.findByKakaoId(user.getKakaoId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER_ID));
        Studio studio = studioRepository.findById(studioId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_STUDIO));
        Scrap scrap = scrapRepository.findByStudioIdAndUserId(studioId, user.getId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_SCRAP));

        if (scrap.getUser().getId().equals(findUser.getId())) {
            studio.removeScrap(scrap);
            studioRepository.save(studio);
        } else {
            throw new CustomException(Error.NOT_MATCH_USER);
        }
    }
}
