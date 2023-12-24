package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findReportByFeedIdAndUserId(Long feedId, Long userId);
    Report findReportByBoardIdAndUserId(Long feedId, Long userId);
}
