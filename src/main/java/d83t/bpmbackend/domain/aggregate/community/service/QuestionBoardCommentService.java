package d83t.bpmbackend.domain.aggregate.community.service;

import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardCommentDto;
import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardCommentReportDto;
import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardCommentResponse;
import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardCommentUpdateDto;
import d83t.bpmbackend.domain.aggregate.user.entity.User;

import java.util.List;

public interface QuestionBoardCommentService {

    QuestionBoardCommentResponse createComment(User user, Long questionBoardArticleId, QuestionBoardCommentDto commentDto);

    List<QuestionBoardCommentResponse> getComments(User user, Long questionBoardArticleId);

    QuestionBoardCommentResponse updateComment(User user, Long questionBoardArticleId, Long commentId, QuestionBoardCommentUpdateDto questionBoardCommentUpdateDto);

    void deleteComment(User user, Long questionBoardArticleId, Long commentId);

    void reportComment(User user, Long questionBoardArticleId, Long commentId, QuestionBoardCommentReportDto reportDto);
}
