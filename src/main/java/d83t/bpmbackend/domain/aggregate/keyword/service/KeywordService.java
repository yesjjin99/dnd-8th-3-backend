package d83t.bpmbackend.domain.aggregate.keyword.service;

import d83t.bpmbackend.domain.aggregate.keyword.dto.KeywordResponse;

import java.util.List;

public interface KeywordService {
    List<KeywordResponse> getAllKeywords();
}
