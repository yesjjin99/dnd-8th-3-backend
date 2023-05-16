package d83t.bpmbackend.domain.aggregate.community.dto;

import d83t.bpmbackend.domain.aggregate.community.entity.Story;
import d83t.bpmbackend.domain.aggregate.community.entity.StoryImage;
import d83t.bpmbackend.domain.aggregate.profile.entity.Profile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Schema(description = "커뮤니티 글 응답 DTO")
public class StoryResponseDto {

    private Long id;
    private String content;
    private List<String> filesPath;
    private AuthorDto author;
    private int likeCount;

    private boolean isLiked;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Builder
    public StoryResponseDto(Story story, boolean isLiked) {
        this.id = story.getId();
        this.content = story.getContent();
        this.likeCount = story.getLikeCount();
        this.isLiked = isLiked;
        this.createdAt = story.getCreatedDate();
        this.updatedAt = story.getModifiedDate();

        List<String> filePaths = new ArrayList<>();
        for (StoryImage image : story.getImages()) {
            filePaths.add(image.getStoragePathName());
        }
        this.filesPath = filePaths;

        Profile profile = story.getAuthor();
        this.author = new AuthorDto(profile.getId(), profile.getNickName(), profile.getStoragePathName());
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
    public static class MultiStories {
        List<StoryResponseDto> stories;
        Integer storyCount;
    }
}
