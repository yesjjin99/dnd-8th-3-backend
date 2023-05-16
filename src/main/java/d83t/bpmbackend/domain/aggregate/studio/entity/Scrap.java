package d83t.bpmbackend.domain.aggregate.studio.entity;

import d83t.bpmbackend.base.entity.DateEntity;
import d83t.bpmbackend.domain.aggregate.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "scraps")
public class Scrap extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studio_id")
    private Studio studio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Scrap(Studio studio, User user) {
        this.studio = studio;
        this.user = user;
    }
}
