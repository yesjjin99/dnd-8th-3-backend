package d83t.bpmbackend.domain.aggregate.studio.service;

import d83t.bpmbackend.domain.aggregate.profile.entity.Profile;
import d83t.bpmbackend.domain.aggregate.profile.repository.ProfileRepository;
import d83t.bpmbackend.domain.aggregate.studio.entity.Like;
import d83t.bpmbackend.domain.aggregate.studio.entity.Review;
import d83t.bpmbackend.domain.aggregate.studio.repository.LikeRepository;
import d83t.bpmbackend.domain.aggregate.studio.repository.ReviewRepository;
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
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Override
    public void createLike(User user, Long reviewId) {
        User findUser = userRepository.findByKakaoId(user.getKakaoId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER_ID));
        Profile profile = findUser.getProfile();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_REVIEW));

        Like like = Like.builder()
                .review(review)
                .user(profile)
                .build();

        review.addLike(like, profile);
        reviewRepository.save(review);
    }

    @Override
    public void deleteLike(User user, Long reviewId) {
        User findUser = userRepository.findByKakaoId(user.getKakaoId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER_ID));
        Profile profile = findUser.getProfile();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_REVIEW));
        Like like = likeRepository.findByReviewIdAndUserId(reviewId, profile.getId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_LIKE));

        // 작성자 검증
        if (like.getUser().getId().equals(profile.getId())) {
            review.removeLike(like);
            reviewRepository.save(review);
        } else {
            throw new CustomException(Error.NOT_MATCH_USER);
        }
    }
}
