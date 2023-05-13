package d83t.bpmbackend.domain.aggregate.studio.service;

import d83t.bpmbackend.domain.aggregate.studio.dto.StudioResponseDto;
import d83t.bpmbackend.domain.aggregate.user.entity.User;

import java.util.List;

public interface ScrapService {
    void createScrap(Long studioId, User user);
    List<StudioResponseDto> findAllScrappedStudio(User user, int page, int size, String sort);
    void deleteScrap(Long studioId, User user);
}
