package d83t.bpmbackend.domain.aggregate.community.repository;

import d83t.bpmbackend.domain.aggregate.community.entity.QuestionBoard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionBoardRepository extends JpaRepository<QuestionBoard, Long> {
    @Query("SELECT a FROM QuestionBoard a WHERE a.author.id= :profileId ORDER BY a.createdDate DESC")
    List<QuestionBoard> findByProfileId(Pageable pageable, @Param("profileId") Long profileId);

    @Query("SELECT a FROM QuestionBoard a ORDER BY a.createdDate DESC")
    List<QuestionBoard> findByAll(Pageable pageable);

    @Query("SELECT a FROM QuestionBoard a WHERE a.slug LIKE %?1%")
    List<QuestionBoard> searchQuestionBoardNames(String query, Pageable pageable);
}
