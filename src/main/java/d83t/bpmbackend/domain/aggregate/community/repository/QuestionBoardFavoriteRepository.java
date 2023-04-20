package d83t.bpmbackend.domain.aggregate.community.repository;

import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoardFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionBoardFavoriteRepository extends JpaRepository<QuestionBoardFavorite, Long> {
    Optional<QuestionBoardFavorite> findByQuestionBoardIdAndUserId(Long questionBoardId, Long userId);
    Long countByQuestionBoardId(Long questionBoardId);
}
