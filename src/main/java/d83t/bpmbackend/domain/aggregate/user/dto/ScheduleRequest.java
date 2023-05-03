package d83t.bpmbackend.domain.aggregate.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Schema(description = "스케줄 등록 요청 DTO")
public class ScheduleRequest {
    @NotBlank(message = "스케줄 이름은 필수입니다.")
    @Schema(description = "스케줄 이름", defaultValue = "여름맞이 눈바디")
    private String scheduleName;
    @Schema(description = "스튜디오 이름", defaultValue = "바디프로필 스튜디오")
    private String studioName;
    @NotBlank(message = "날짜는 필수입니다.")
    @Schema(description = "날짜", defaultValue = "2022-01-01")
    private String date;
    @Schema(description = "시간", defaultValue = "00:00:00")
    private String time;
    @Schema(description = "메모", defaultValue = "이번엔 열심히 아자아자 화이팅!")
    private String memo;
}
