package d83t.bpmbackend.domain.aggregate.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "카카오 로그인 API 응답 DTO")
public class ProfileResponse {
    private String nickname;
    private String bio;
    private String token;
    private String image;

}
