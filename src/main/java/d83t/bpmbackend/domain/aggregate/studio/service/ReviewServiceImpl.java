package d83t.bpmbackend.domain.aggregate.studio.service;

import d83t.bpmbackend.domain.aggregate.profile.entity.Profile;
import d83t.bpmbackend.domain.aggregate.profile.repository.ProfileRepository;
import d83t.bpmbackend.domain.aggregate.studio.dto.ReviewRequestDto;
import d83t.bpmbackend.domain.aggregate.studio.dto.ReviewResponseDto;
import d83t.bpmbackend.domain.aggregate.studio.entity.Review;
import d83t.bpmbackend.domain.aggregate.studio.entity.ReviewImage;
import d83t.bpmbackend.domain.aggregate.studio.entity.Studio;
import d83t.bpmbackend.domain.aggregate.studio.repository.LikeRepository;
import d83t.bpmbackend.domain.aggregate.studio.repository.ReviewRepository;
import d83t.bpmbackend.domain.aggregate.studio.repository.StudioRepository;
import d83t.bpmbackend.domain.aggregate.user.entity.User;
import d83t.bpmbackend.domain.aggregate.user.repository.UserRepository;
import d83t.bpmbackend.exception.CustomException;
import d83t.bpmbackend.exception.Error;
import d83t.bpmbackend.s3.S3UploaderService;
import d83t.bpmbackend.utils.FileUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final StudioRepository studioRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final S3UploaderService uploaderService;

    @Value("${bpm.s3.bucket.review.path}")
    private String reviewPath;

    @Value("${bpm.s3.bucket.base}")
    private String basePath;

    @Value("${spring.environment}")
    private String env;

    private String fileDir;

    @PostConstruct
    private void init() {
        if (env.equals("local")) {
            this.fileDir = FileUtils.getUploadPath();
        } else if (env.equals("prod")) {
            this.fileDir = this.basePath + this.reviewPath;
        }
    }

    @Override
    @Transactional
    public ReviewResponseDto createReview(Long studioId, User user, List<MultipartFile> files, ReviewRequestDto requestDto) {
        if (files == null || files.isEmpty()) {
            throw new CustomException(Error.FILE_REQUIRED);
        }
        if (files.size() > 5) {
            throw new CustomException(Error.FILE_SIZE_MAX);
        }

        List<String> filePaths = new ArrayList<>();
        User findUser = userRepository.findByKakaoId(user.getKakaoId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER_ID));
        Studio studio = studioRepository.findById(studioId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_STUDIO));
        Profile profile = findUser.getProfile();

        Review review = requestDto.toEntity(studio, profile);

        for (MultipartFile file : files) {
            String newName = FileUtils.createNewFileName(file.getOriginalFilename());
            String filePath = fileDir + newName;

            review.addReviewImage(ReviewImage.builder()
                    .originFileName(newName)
                    .storagePathName(filePath)
                    .review(review)
                    .build());
            filePaths.add(filePath);

            if (env.equals("prod")) {
                uploaderService.putS3(file, reviewPath, newName);
            } else if (env.equals("local")) {
                try {
                    File localFile = new File(filePath);
                    file.transferTo(localFile);
                    FileUtils.removeNewFile(localFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Review savedReview = reviewRepository.save(review);
        studio.addReview(savedReview);
        studio.addRecommend(requestDto.getRecommends());
        studioRepository.save(studio);

        return new ReviewResponseDto(savedReview, false);
    }

    @Override
    public List<ReviewResponseDto> findAll(User user, Long studioId, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
        Page<Review> reviews = reviewRepository.findByStudioId(studioId, pageable);

        User findUser = userRepository.findByKakaoId(user.getKakaoId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER_ID));
        Profile profile = findUser.getProfile();

        return reviews.stream().map(review -> {
                    boolean isLiked = checkReviewLiked(review.getId(), profile.getId());
                    return new ReviewResponseDto(review, isLiked);
                }).collect(Collectors.toList());
    }

    @Override
    public ReviewResponseDto findById(User user, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_REVIEW));

        User findUser = userRepository.findByKakaoId(user.getKakaoId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER_ID));
        Profile profile = findUser.getProfile();

        boolean isLiked = checkReviewLiked(review.getId(), profile.getId());
        return new ReviewResponseDto(review, isLiked);
    }

    @Override
    @Transactional
    public ReviewResponseDto updateReview(User user, Long studioId, Long reviewId, List<MultipartFile> files, ReviewRequestDto requestDto) {
        if (files == null || files.isEmpty()) {
            throw new CustomException(Error.FILE_REQUIRED);
        }
        if (files.size() > 5) {
            throw new CustomException(Error.FILE_SIZE_MAX);
        }

        User findUser = userRepository.findByKakaoId(user.getKakaoId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER_ID));
        Studio studio = studioRepository.findById(studioId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_STUDIO));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_REVIEW));

        // 작성자 검증
        Long profileId = findUser.getProfile().getId();
        if (!review.getAuthor().getId().equals(profileId)) {
            throw new CustomException(Error.NOT_MATCH_USER);
        }

        if (requestDto.getRating() != null) {
            review.setRating(requestDto.getRating());
        }
        if (requestDto.getRecommends() != null) {
            review.setRecommends(requestDto.getRecommends());
        }
        if (requestDto.getContent() != null) {
            review.setContent(requestDto.getContent());
        }
        boolean isLiked = checkReviewLiked(review.getId(), profileId);

        List<String> filePaths = new ArrayList<>();
        List<ReviewImage> reviewImages = new ArrayList<>();
        for (MultipartFile file : files) {
            String newName = FileUtils.createNewFileName(file.getOriginalFilename());
            String filePath = fileDir + newName;

            reviewImages.add(ReviewImage.builder()
                    .originFileName(newName)
                    .storagePathName(filePath)
                    .review(review)
                    .build());
            filePaths.add(filePath);

            if (env.equals("prod")) {
                uploaderService.putS3(file, reviewPath, newName);
            } else if (env.equals("local")) {
                try {
                    File localFile = new File(filePath);
                    file.transferTo(localFile);
                    FileUtils.removeNewFile(localFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        review.updateReviewImage(reviewImages);

        Review savedReview = reviewRepository.save(review);
        return new ReviewResponseDto(savedReview, isLiked);
    }

    @Override
    @Transactional
    public void deleteReview(User user, Long studioId, Long reviewId) {
        Studio studio = studioRepository.findById(studioId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_STUDIO));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_REVIEW));
        User findUser = userRepository.findByKakaoId(user.getKakaoId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER_ID));

        if (review.getAuthor().getId().equals(findUser.getProfile().getId())) {
            studio.removeRecommend(review.getRecommends());
            studio.removeReview(review);
            studioRepository.save(studio);
        } else {
            throw new CustomException(Error.NOT_MATCH_USER);
        }
    }

    private boolean checkReviewLiked(Long reviewId, Long profileId) {
        boolean isLiked = false;
        if (likeRepository.existsByReviewIdAndUserId(reviewId, profileId)) {
            isLiked = true;
        }
        return isLiked;
    }
}
