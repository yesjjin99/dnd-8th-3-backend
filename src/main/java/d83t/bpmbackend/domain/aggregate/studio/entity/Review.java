package d83t.bpmbackend.domain.aggregate.studio.entity;

import d83t.bpmbackend.base.entity.DateEntity;
import d83t.bpmbackend.domain.aggregate.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
public class Review extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studio_id")
    private Studio studio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Profile author;

    @Column
    private Double rating;

    @ElementCollection
    @CollectionTable(name = "review_recommends", joinColumns = @JoinColumn(name = "review_id"))
    @Column
    private List<String> recommends = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> images;

    @Column
    private String content;

    @Column(columnDefinition = "int default 0")
    private int likeCount;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @Builder
    public Review(Studio studio, Profile author, Double rating, List<String> recommends, List<ReviewImage> images, String content, int likeCount) {
        this.studio = studio;
        this.author = author;
        this.rating = rating;
        this.recommends = recommends;
        this.images = images;
        this.content = content;
        this.likeCount = likeCount;
    }

    public void addReviewImage(ReviewImage image) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        this.images.add(image);
    }

    public void updateReviewImage(List<ReviewImage> images) {
        this.images.clear();
        this.images.addAll(images);
    }

    public void setStudio(Studio studio) {
        this.studio = studio;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setRecommends(List<String> recommends) {
        this.recommends = recommends;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void addLike(Like like, Profile user) {
        this.likes.add(like);
        this.likeCount += 1;
    }

    public void removeLike(Like like) {
        this.likes.remove(like);
        this.likeCount -= 1;
    }
}
