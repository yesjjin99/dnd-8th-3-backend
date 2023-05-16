package d83t.bpmbackend.domain.aggregate.community.entity;

import d83t.bpmbackend.base.entity.DateEntity;
import d83t.bpmbackend.domain.aggregate.profile.entity.Profile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "questionBoard_comment")
public class QuestionBoardComment extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Profile author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private QuestionBoard questionBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private QuestionBoardComment parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<QuestionBoardComment> children = new ArrayList<>();

    @Builder.Default
    private int reportCount = 0;

    // 부모 댓글 수정
    public void updateParent(QuestionBoardComment parent){
        this.parent = parent;
    }

    //글 내용 수정
    public void updateBody(String updateBody) {
        this.body = updateBody;
    }
    // 신고수 추가
    public void plusReport(){
        this.reportCount += 1;
    }
}
