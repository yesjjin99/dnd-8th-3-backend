package d83t.bpmbackend.domain.aggregate.community.service;

import d83t.bpmbackend.domain.aggregate.community.entity.Story;
import d83t.bpmbackend.domain.aggregate.community.entity.StoryLike;
import d83t.bpmbackend.domain.aggregate.community.repository.StoryLikeRepository;
import d83t.bpmbackend.domain.aggregate.community.repository.StoryRepository;
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
public class StoryLikeServiceImpl implements StoryLikeService {

    private final StoryLikeRepository storyLikeRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;

    @Override
    public void createStoryLike(Long storyId, User user) {
        User findUser = userRepository.findByKakaoId(user.getKakaoId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER_ID));
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_STORY));

        StoryLike storyLike = StoryLike.builder()
                .story(story)
                .user(findUser)
                .build();

        story.addStoryLike(storyLike);
        storyRepository.save(story);
    }

    @Override
    public void deleteStoryLike(Long storyId, User user) {
        User findUser = userRepository.findByKakaoId(user.getKakaoId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER_ID));
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_STORY));

        StoryLike storyLike = storyLikeRepository.findByStoryIdAndUserId(storyId, findUser.getId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_LIKE));

        if (storyLike.getUser().getId().equals(findUser.getId())) {
            story.removeStoryLike(storyLike);
            storyRepository.save(story);
        } else {
            throw new CustomException(Error.NOT_MATCH_USER);
        }
    }
}
