package d83t.bpmbackend.domain.aggregate.community.entity;

import d83t.bpmbackend.base.entity.DateEntity;
import d83t.bpmbackend.domain.aggregate.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "questionBoard")
public class QuestionBoard extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String slug;

    @Column
    private String content;

    @OneToMany(mappedBy = "questionBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionBoardImage> image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Profile author;

    @OneToMany(mappedBy = "questionBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionBoardComment> comments;

    @OneToMany(mappedBy = "questionBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionBoardFavorite> favorites;

    public void addQuestionBoardImage(QuestionBoardImage questionBoardImage){
        if(this.image == null){
            this.image = new ArrayList<>();
        }
        this.image.add(questionBoardImage);
    }

    public void changeTitle(String title){
        this.slug = title;
    }

    public void changeContent(String content){
        this.content = content;
    }

    public void changeImage(List<QuestionBoardImage> questionBoardImage){
        this.image.clear();
        this.image.addAll(questionBoardImage);
    }

}
