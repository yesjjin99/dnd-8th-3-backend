package d83t.bpmbackend.domain.aggregate.keyword.repository;

import d83t.bpmbackend.domain.aggregate.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}
