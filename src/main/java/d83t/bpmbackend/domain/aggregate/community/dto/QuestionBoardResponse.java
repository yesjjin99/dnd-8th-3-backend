package d83t.bpmbackend.domain.aggregate.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Builder
@Getter
public class QuestionBoardResponse {

    @Schema(description = "게시글의 id", defaultValue = "1")
    private Long id;

    @Schema(description = "게시글의 제목", defaultValue = "제목입니다.")
    private String title;

    @Schema(description = "게시글의 본문", defaultValue = "본문입니다.")
    private String content;

    @Schema(description = "생성 시간")
    private ZonedDateTime createdAt;
    @Schema(description = "갱신 시간")
    private ZonedDateTime updatedAt;

    @Schema(description = "등록된 이미지 경로들", defaultValue = "https://s3이미지경로")
    private List<String> filesPath;

    @Schema(description = "작성자")
    private QuestionBoardResponse.Author author;

    @Schema(description = "좋아요 여부")
    private Boolean favorited;

    @Schema(description = "게시글 좋아요 갯수")
    private Long favoritesCount;

    @Builder
    @Getter
    public static class Author{
        @Schema(description = "작성자 닉네임", defaultValue = "nickname")
        private String nickname;
        @Schema(description = "작성자 프로필 경로",  defaultValue = "https://s3이미지경로")
        private String profilePath;
    }

    @Builder
    @Getter
    public static class SingleQuestionBoard{
        QuestionBoardResponse questionBoardResponse;
    }

    @Builder
    @Getter
    public static class MultiQuestionBoard{
        List<QuestionBoardResponse> questionBoardResponseList;
        Integer questionBoardCount;
    }
}
