package d83t.bpmbackend.domain.aggregate.studio.dto;

import d83t.bpmbackend.domain.aggregate.community.dto.BodyShapeResponse;
import d83t.bpmbackend.domain.aggregate.studio.entity.Studio;
import d83t.bpmbackend.domain.aggregate.studio.entity.StudioImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Schema(description = "스튜디오 응답 DTO")
public class StudioResponseDto {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String firstTag;
    private String secondTag;
    private Map<String, Integer> topRecommends;
    private String phone;
    private String sns;
    private String openHours;
    private String price;
    private List<String> filesPath;
    private String content;
    private Double rating;
    private int reviewCount;
    private int scrapCount;

    private boolean isScrapped;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Builder
    public StudioResponseDto(Studio studio, boolean isScrapped) {
        this.id = studio.getId();
        this.name = studio.getName();
        this.address = studio.getAddress();
        this.latitude = studio.getLatitude();
        this.longitude = studio.getLongitude();
        this.firstTag = studio.getFirstTag();
        this.secondTag = studio.getSecondTag();
        this.topRecommends = studio.getTopRecommends();
        this.phone = studio.getPhone();
        this.sns = studio.getSns();
        this.openHours = studio.getOpenHours();
        this.price = studio.getPrice();

        List<String> filePaths = new ArrayList<>();
        for (StudioImage image : studio.getImages()) {
            filePaths.add(image.getStoragePathName());
        }
        this.filesPath = filePaths;

        this.content = studio.getContent();
        this.rating = studio.getRating();
        this.reviewCount = studio.getReviewCount();
        this.scrapCount = studio.getScrapCount();
        this.isScrapped = isScrapped;
        this.createdAt = studio.getCreatedDate();
        this.updatedAt = studio.getModifiedDate();
    }

    @Builder
    @Getter
    public static class MultiStudios{
        List<StudioResponseDto> studios;
        Integer studiosCount;
    }
}
