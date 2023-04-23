package d83t.bpmbackend.domain.aggregate.community.service;

import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardParam;
import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardRequest;
import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardResponse;
import d83t.bpmbackend.domain.aggregate.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionBoardService {
    QuestionBoardResponse createQuestionBoardArticle(User user, List<MultipartFile> files, QuestionBoardRequest questionBoardRequest);

    List<QuestionBoardResponse> getQuestionBoardArticles(User user, Integer limit, Integer offset, QuestionBoardParam questionBoardParam);

    QuestionBoardResponse getQuestionBoardArticle(User user, Long questionBoardArticleId);

    QuestionBoardResponse updateQuestionBoardArticle(User user, List<MultipartFile> files, QuestionBoardRequest questionBoardRequest, Long questionBoardArticleId);
    
    void deleteQuestionBoardArticle(User user, Long questionBoardArticleId);

    QuestionBoardResponse favoriteQuestionBoardArticle(User user, Long questionBoardArticleId);

    QuestionBoardResponse unfavoriteQuestionBoardArticle(User user, Long questionBoardArticleId);
}
