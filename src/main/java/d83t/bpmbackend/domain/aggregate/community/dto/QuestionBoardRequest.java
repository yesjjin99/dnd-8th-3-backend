package d83t.bpmbackend.domain.aggregate.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@ToString
public class QuestionBoardRequest {

    @Schema(description = "게시글의 제목", defaultValue = "게시글의 제목입니다.")
    private String title;

    @Schema(description = "게시글의 본문", defaultValue = "게시글 본문입니다.")
    private String content;
}
