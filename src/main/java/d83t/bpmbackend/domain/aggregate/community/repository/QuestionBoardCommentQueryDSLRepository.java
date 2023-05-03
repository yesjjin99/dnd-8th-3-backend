package d83t.bpmbackend.domain.aggregate.community.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import d83t.bpmbackend.domain.aggregate.community.entity.QQuestionBoardComment;
import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoard;
import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoardComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QuestionBoardCommentQueryDSLRepository {
    private final JPAQueryFactory jpaQueryFactory;

    // 게시글의 댓글 전체 가져오기
    public List<QuestionBoardComment> findAllByQuestionComment(QuestionBoard questionBoard){
        return jpaQueryFactory.selectFrom(QQuestionBoardComment.questionBoardComment)
                .leftJoin(QQuestionBoardComment.questionBoardComment.parent)
                .fetchJoin()
                .where(QQuestionBoardComment.questionBoardComment.questionBoard.id.eq(questionBoard.getId()))
                .orderBy(QQuestionBoardComment.questionBoardComment.parent.id.asc().nullsFirst(), QQuestionBoardComment.questionBoardComment.createdDate.asc())
                .fetch();
    }

}
