package d83t.bpmbackend.domain.aggregate.studio.service;

import d83t.bpmbackend.domain.aggregate.user.entity.User;

public interface ScrapService {
    void createScrap(Long studioId, User user);
    void deleteScrap(Long studioId, User user);
}
