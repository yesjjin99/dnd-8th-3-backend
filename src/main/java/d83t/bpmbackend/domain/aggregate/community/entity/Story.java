package d83t.bpmbackend.domain.aggregate.community.entity;

import d83t.bpmbackend.base.entity.DateEntity;
import d83t.bpmbackend.domain.aggregate.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "Story")
public class Story extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoryImage> images;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Profile author;

    @Column(columnDefinition = "int default 0")
    private int likeCount;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoryLike> likes = new ArrayList<>();

    public void addStoryImage(StoryImage storyImage) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        this.images.add(storyImage);
    }

    public void updateStoryImage(List<StoryImage> images) {
        this.images.clear();
        this.images.addAll(images);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void addStoryLike(StoryLike like) {
        this.likes.add(like);
        this.likeCount += 1;
    }

    public void removeStoryLike(StoryLike like) {
        this.likes.remove(like);
        this.likeCount -= 1;
    }
}
