package d83t.bpmbackend.domain.aggregate.studio.repository;

import d83t.bpmbackend.domain.aggregate.studio.entity.Scrap;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    Optional<Scrap> findByStudioIdAndUserId(Long studioId, Long userId);
    boolean existsByStudioIdAndUserId(Long studioId, Long userId);
    Page<Scrap> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT Scrap.studio.id FROM Scrap WHERE Scrap.user.id = :userId AND Scrap.studio.id IN :studioIds")
    List<Long> findScrappedStudioIdsByUserIdAndStudioIds(@Param("userId") Long userId, @Param("studioIds") List<Long> studioIds);
}
