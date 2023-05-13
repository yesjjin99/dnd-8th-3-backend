package d83t.bpmbackend.domain.aggregate.community.entity;

import d83t.bpmbackend.base.entity.DateEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "questionBoard_comment_report")
public class QuestionBoardCommentReport extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive
    private Long commentId;

    @NotBlank
    private String commentBody;

    @NotBlank
    private String commentAuthor;

    @NotNull
    private ZonedDateTime commentCreatedAt;

    @NotNull
    private ZonedDateTime commentUpdatedAt;

    @NotBlank
    private String reportReason;

    @NotNull
    @Positive
    private Long reporter;

}
