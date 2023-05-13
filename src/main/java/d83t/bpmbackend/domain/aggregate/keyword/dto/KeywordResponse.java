package d83t.bpmbackend.domain.aggregate.keyword.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class KeywordResponse {

    private Long id;
    private String keyword;

    @Builder
    @Getter
    public static class MultiKeyword{
        List<KeywordResponse> keywords;
    }

}
