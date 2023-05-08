package d83t.bpmbackend.domain.aggregate.studio.repository;

import d83t.bpmbackend.domain.aggregate.studio.entity.Scrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    Optional<Scrap> findByStudioIdAndUserId(Long studioId, Long userId);
    boolean existsByStudioIdAndUserId(Long studioId, Long userId);
    Page<Scrap> findByUserId(Long userId, Pageable pageable);
}
