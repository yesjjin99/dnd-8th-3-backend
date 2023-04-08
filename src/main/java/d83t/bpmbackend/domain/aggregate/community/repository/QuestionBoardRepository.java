package d83t.bpmbackend.domain.aggregate.community.repository;

import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionBoardRepository extends JpaRepository<QuestionBoard, Long> {
}
