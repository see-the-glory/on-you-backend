package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.ClubSchedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClubScheduleRepository extends JpaRepository<ClubSchedule, Long> {
    List<ClubSchedule> findByClubIdAndEndDateAfterOrderByStartDate(Long clubId, LocalDateTime now);
}
