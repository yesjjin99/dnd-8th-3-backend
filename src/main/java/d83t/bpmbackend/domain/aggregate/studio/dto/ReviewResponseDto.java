package d83t.bpmbackend.domain.aggregate.studio.dto;

import d83t.bpmbackend.domain.aggregate.profile.entity.Profile;
import d83t.bpmbackend.domain.aggregate.studio.entity.Review;
import d83t.bpmbackend.domain.aggregate.studio.entity.ReviewImage;
import d83t.bpmbackend.domain.aggregate.studio.entity.Studio;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Schema(description = "리뷰 응답 DTO")
public class ReviewResponseDto {
    private Long id;
    private StudioDto studio;
    private AuthorDto author;
    private Double rating;
    private List<String> recommends;
    private List<String> filesPath;
    private String content;
    private int likeCount;

    private boolean isLiked;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;


    @Builder
    public ReviewResponseDto(Review review, boolean isLiked) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.recommends = review.getRecommends();
        this.content = review.getContent();
        this.likeCount = review.getLikeCount();
        this.isLiked = isLiked;
        this.createdAt = review.getCreatedDate();
        this.updatedAt = review.getModifiedDate();

        Studio studio = review.getStudio();
        this.studio = new StudioDto(studio.getId(), studio.getName(), studio.getRating(), studio.getContent());
        Profile profile = review.getAuthor();
        this.author = new AuthorDto(profile.getId(), profile.getNickName(), profile.getStoragePathName());

        List<String> filePaths = new ArrayList<>();
        for (ReviewImage image : review.getImages()) {
            filePaths.add(image.getStoragePathName());
        }
        this.filesPath = filePaths;
    }

    @Builder
    @Getter
    public static class StudioDto {
        private Long id;
        private String name;
        private Double rating;
        private String content;
    }

    @Builder
    @Getter
    public static class AuthorDto {
        private Long id;
        private String nickname;
        private String profilePath;
    }

    @Builder
    @Getter
    public static class MultiReviews {
        List<ReviewResponseDto> reviews;
        Integer reviewCount;
    }
}
