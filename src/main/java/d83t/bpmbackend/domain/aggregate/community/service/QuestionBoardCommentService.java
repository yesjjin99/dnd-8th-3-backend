package d83t.bpmbackend.domain.aggregate.community.service;

import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardCommentDto;
import d83t.bpmbackend.domain.aggregate.community.dto.QuestionBoardCommentResponse;
import d83t.bpmbackend.domain.aggregate.user.entity.User;

public interface QuestionBoardCommentService {

    QuestionBoardCommentResponse createComment(User user, Long questionBoardArticleId, QuestionBoardCommentDto commentDto);
}
