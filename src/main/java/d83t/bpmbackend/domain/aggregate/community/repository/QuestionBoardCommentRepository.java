package d83t.bpmbackend.domain.aggregate.community.repository;

import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionBoardCommentRepository extends JpaRepository<QuestionBoardComment, Long> {
}
