package d83t.bpmbackend.domain.aggregate.studio.service;

import d83t.bpmbackend.domain.aggregate.studio.dto.StudioRequestDto;
import d83t.bpmbackend.domain.aggregate.studio.dto.StudioResponseDto;
import d83t.bpmbackend.domain.aggregate.user.entity.User;

import java.util.List;

public interface StudioService {
    List<StudioResponseDto> searchStudio(String q, User user);
    StudioResponseDto createStudio(StudioRequestDto requestDto);
    StudioResponseDto findById(Long studioId, User user);

    List<StudioResponseDto> findStudioAll(Integer limit, Integer offset, User user);
}
