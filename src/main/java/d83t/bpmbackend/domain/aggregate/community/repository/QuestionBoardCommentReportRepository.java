package d83t.bpmbackend.domain.aggregate.community.repository;

import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoardCommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionBoardCommentReportRepository extends JpaRepository<QuestionBoardCommentReport, Long > {
}
