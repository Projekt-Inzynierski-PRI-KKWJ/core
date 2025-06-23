package pl.edu.amu.wmi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.amu.wmi.entity.ExternalLinkHistory;

import java.util.List;

@Repository
public interface ExternalLinkHistoryDAO extends JpaRepository<ExternalLinkHistory, Long> {

    List<ExternalLinkHistory> findByExternalLinkIdOrderByCreationDateDesc(Long externalLinkId);

    @Query("SELECT h FROM ExternalLinkHistory h WHERE h.externalLink.id = :externalLinkId ORDER BY h.creationDate DESC")
    List<ExternalLinkHistory> findHistoryByExternalLinkId(@Param("externalLinkId") Long externalLinkId);

    @Query("SELECT h FROM ExternalLinkHistory h JOIN h.externalLink el JOIN el.externalLinkDefinition eld " +
           "WHERE eld.studyYear.studyYear = :studyYear ORDER BY h.creationDate DESC")
    List<ExternalLinkHistory> findAllHistoryByStudyYear(@Param("studyYear") String studyYear);

}
