package d83t.bpmbackend.domain.aggregate.community.service;

import d83t.bpmbackend.domain.aggregate.user.entity.User;

public interface StoryLikeService {
    void createStoryLike(Long storyId, User user);
    void deleteStoryLike(Long storyId, User user);
}
