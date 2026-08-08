package d83t.bpmbackend.domain.aggregate.studio.repository;

import d83t.bpmbackend.domain.aggregate.studio.entity.Like;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByReviewIdAndUserId(Long reviewId, Long userId);
    boolean existsByReviewIdAndUserId(Long reviewId, Long userId);

    @Query("SELECT Like.review.id FROM Like WHERE Like.user.id = :userId AND Like.review.id IN :reviewIds")
    List<Long> findLikedReviewIdsByUserIdAndReviewIds(@Param("userId") Long userId, @Param("reviewIds") List<Long> reviewIds);
}
