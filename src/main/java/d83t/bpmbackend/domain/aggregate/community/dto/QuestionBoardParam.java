package d83t.bpmbackend.domain.aggregate.community.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QuestionBoardParam {
    private String nickname;
    private String slug;
}
