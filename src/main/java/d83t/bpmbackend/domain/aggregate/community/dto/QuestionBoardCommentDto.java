package d83t.bpmbackend.domain.aggregate.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
public class QuestionBoardCommentDto {
    @Schema(description = "댓글 내용", defaultValue = "댓글 내용입니다.")
    private String body;
}
