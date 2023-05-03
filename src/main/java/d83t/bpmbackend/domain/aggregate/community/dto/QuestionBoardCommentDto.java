package d83t.bpmbackend.domain.aggregate.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
public class QuestionBoardCommentDto {
    @Schema(description = "부모 댓글 ID", defaultValue = "1L")
    private Long parentId;

    @Schema(description = "댓글 내용", defaultValue = "댓글 내용입니다.")
    private String body;
}
