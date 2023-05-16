package d83t.bpmbackend.domain.aggregate.keyword.service;

import d83t.bpmbackend.domain.aggregate.keyword.dto.KeywordResponse;
import d83t.bpmbackend.domain.aggregate.keyword.entity.Keyword;
import d83t.bpmbackend.domain.aggregate.keyword.repository.KeywordRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService {

    public ConcurrentHashMap<Long, String> keywordSymbolMap = new ConcurrentHashMap<>();

    private final KeywordRepository keywordRepository;

    //서버 뜰때 키워드들을 가져온다.
    @PostConstruct
    public void init() {
        List<Keyword> keywords = keywordRepository.findAll();
        for (Keyword keyword : keywords) {
            // 각 키워드의 상징값을 계산하여 Map에 저장
            keywordSymbolMap.put(keyword.getId(), keyword.getKeyword());
        }
    }

    @Override
    public List<KeywordResponse> getAllKeywords() {
        return keywordSymbolMap.entrySet()
                .stream()
                .map(entry -> KeywordResponse.builder()
                        .id(entry.getKey())
                        .keyword(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

}
