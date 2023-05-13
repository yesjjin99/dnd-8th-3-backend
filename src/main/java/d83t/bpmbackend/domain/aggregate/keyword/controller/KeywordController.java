package d83t.bpmbackend.domain.aggregate.keyword.controller;

import d83t.bpmbackend.domain.aggregate.keyword.dto.KeywordResponse;
import d83t.bpmbackend.domain.aggregate.keyword.service.KeywordService;
import d83t.bpmbackend.domain.aggregate.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class KeywordController {

    private final KeywordService keywordService;

    @GetMapping("/api/keywords")
    public KeywordResponse.MultiKeyword getAllKeywords(@AuthenticationPrincipal User user){
        return KeywordResponse.MultiKeyword.builder().keywords(keywordService.getAllKeywords()).build();
    }
}
