package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.ClubSchedule;

import java.util.Optional;

public interface ClubScheduleRepository extends JpaRepository<ClubSchedule, Long> {
    Optional<ClubSchedule> findByClubId(Long clubId);
}
