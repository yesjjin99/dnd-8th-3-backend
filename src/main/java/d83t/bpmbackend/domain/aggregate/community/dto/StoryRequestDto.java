package d83t.bpmbackend.domain.aggregate.community.dto;

import d83t.bpmbackend.domain.aggregate.community.entity.Story;
import d83t.bpmbackend.domain.aggregate.profile.entity.Profile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "커뮤니티 글 작성 요청 DTO")
public class StoryRequestDto {

    private String content;

    public Story toEntity(Profile profile) {
        return Story.builder()
                .content(content)
                .author(profile)
                .build();
    }
}
