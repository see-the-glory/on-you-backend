package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.UserClub;
import stg.onyou.model.entity.UserClubSchedule;

import java.util.Optional;

public interface UserClubScheduleRepository extends JpaRepository<UserClubSchedule, Long> {
    Optional<UserClubSchedule> findByUserIdAndClubScheduleId(Long userId, Long clubScheduleId);
    Optional<UserClubSchedule> findByClubScheduleId(Long clubScheduleId);
}
