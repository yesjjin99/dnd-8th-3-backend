package d83t.bpmbackend.domain.aggregate.studio.entity;

import d83t.bpmbackend.base.entity.DateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "studio")
public class Studio extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String address;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    private String firstTag;

    @Column
    private String secondTag;

    @ElementCollection
    @CollectionTable(name = "studio_recommends", joinColumns = @JoinColumn(name = "studio_id"))
    @MapKeyColumn(name = "recommend")
    @Column(name = "count")
    private Map<String, Integer> recommends = new HashMap<>();

    @Column
    private String phone;

    @Column
    private String sns;

    @Column
    private String openHours;

    @Column
    private String price;

    @OneToMany(mappedBy = "studio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudioImage> images;

    @Column
    private String content;

    @Column(columnDefinition = "double precision default 0.0")
    private Double rating;

    @Column(columnDefinition = "int default 0")
    private int reviewCount;

    @Column(columnDefinition = "int default 0")
    private int scrapCount;

    @OneToMany(mappedBy = "studio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "studio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Scrap> scraps = new ArrayList<>();

    @Builder
    public Studio(String name, String address, Double latitude, Double longitude, String firstTag, String secondTag, String phone, String sns, String openHours, String price, List<StudioImage> images, String content, Double rating, int reviewCount, int scrapCount) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.firstTag = firstTag;
        this.secondTag = secondTag;
        this.phone = phone;
        this.sns = sns;
        this.openHours = openHours;
        this.price = price;
        this.images = images;
        this.content = content;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.scrapCount = scrapCount;
    }

    public void addRecommend(List<String> recommends) {
        for (String recommend : recommends) {
            this.recommends.putIfAbsent(recommend, 0);
            this.recommends.put(recommend, this.recommends.get(recommend) + 1);
        }
    }

    public void removeRecommend(List<String> recommends) {
        for (String recommend : recommends) {
            if (this.recommends.containsKey(recommend)) {
                int count = this.recommends.get(recommend);
                if (count > 1) {
                    this.recommends.put(recommend, count - 1);
                } else {
                    this.recommends.remove(recommend);
                }
            }
        }
    }

    public Map<String, Integer> getTopRecommends() {
        Map<String, Integer> topRecommends = new LinkedHashMap<>();

        List<Map.Entry<String, Integer>> sortedRecommends = new ArrayList<>(this.recommends.entrySet());
        Collections.sort(sortedRecommends, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        int i = 0;
        for (Map.Entry<String, Integer> recommend : sortedRecommends) {
            topRecommends.put(recommend.getKey(), recommend.getValue());
            i++;
            if (i == 10) {
                break;
            }
        }
        return topRecommends;
    }

    public void addStudioImage(StudioImage studioImage) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        this.images.add(studioImage);
    }

    public Review addReview(Review review) {
        this.reviews.add(review);
        if (review.getRating() != 0.0) {
            Double avg = ((this.rating * reviewCount) + review.getRating()) / (reviewCount + 1);
            this.rating = avg;
        }
        this.reviewCount += 1;
        return review;
    }

    public void removeReview(Review review) {
        this.reviews.remove(review);
        if (review.getRating() != 0.0) {
            Double avg = ((this.rating * reviewCount) - review.getRating()) / (reviewCount - 1);
            this.rating = avg;
        }
        this.reviewCount -= 1;
    }

    public void addScrap(Scrap scrap) {
        this.scraps.add(scrap);
        this.scrapCount += 1;
    }

    public void removeScrap(Scrap scrap) {
        this.scraps.remove(scrap);
        this.scrapCount -= 1;
    }
}
