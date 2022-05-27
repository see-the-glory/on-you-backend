package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.ClubSchedule;

public interface ClubScheduleRepository extends JpaRepository<ClubSchedule, Long> {
}
