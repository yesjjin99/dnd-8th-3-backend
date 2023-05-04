package d83t.bpmbackend.domain.aggregate.studio.dto;

import d83t.bpmbackend.domain.aggregate.studio.entity.Studio;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "스튜디오 등록 요청 DTO")
public class StudioRequestDto {
    @NotBlank(message = "스튜디오 이름은 필수입니다")
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private List<String> recommends;
    private String phone;
    private String sns;
    private String openHours;
    private String price;

    public Studio toEntity() {
        String[] addr = address.split(" ");

        return Studio.builder()
            .name(name)
            .address(address)
            .latitude(latitude)
            .longitude(longitude)
            .firstTag(addr[0])
            .secondTag(addr[1])
            .phone(phone)
            .sns(sns)
            .openHours(openHours)
            .price(price)
            .build();
    }
}
