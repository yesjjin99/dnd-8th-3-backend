package d83t.bpmbackend.domain.aggregate.keyword.service;

import d83t.bpmbackend.domain.aggregate.keyword.dto.KeywordResponse;
import d83t.bpmbackend.domain.aggregate.keyword.entity.Keyword;
import d83t.bpmbackend.domain.aggregate.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService{

    private final KeywordRepository keywordRepository;

    @Override
    public List<KeywordResponse> getAllKeywords() {
        List<Keyword> keywords = keywordRepository.findAll();
        return keywords.stream().map(keyword -> {
            return KeywordResponse.builder()
                    .id(keyword.getId())
                    .keyword(keyword.getKeyword())
                    .build();
        }).collect(Collectors.toList());

    }
}
