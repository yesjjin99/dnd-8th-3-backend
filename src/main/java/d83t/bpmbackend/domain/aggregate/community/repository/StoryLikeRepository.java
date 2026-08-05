package d83t.bpmbackend.domain.aggregate.community.repository;

import d83t.bpmbackend.domain.aggregate.community.entity.StoryLike;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoryLikeRepository extends JpaRepository<StoryLike, Long> {
    Optional<StoryLike> findByStoryIdAndUserId(Long storyId, Long userId);
    boolean existsByStoryIdAndUserId(Long storyId, Long userId);

    @Query("SELECT sl.story.id FROM StoryLike sl WHERE sl.user.id = :userId AND sl.story.id IN :storyIds")
    List<Long> findLikedStoryIdsByUserIdAndStoryIds(@Param("userId") Long userId, @Param("storyIds") List<Long> storyIds);
}
