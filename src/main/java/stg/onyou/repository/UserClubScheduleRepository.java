package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import stg.onyou.model.entity.UserClub;
import stg.onyou.model.entity.UserClubSchedule;

import java.util.Optional;

public interface UserClubScheduleRepository extends JpaRepository<UserClubSchedule, Long> {
    Optional<UserClubSchedule> findByUserIdAndClubScheduleId(Long userId, Long clubScheduleId);
    Optional<UserClubSchedule> findByClubScheduleId(Long clubScheduleId);
    @Query("delete from UserClubSchedule ucs where ucs.user.id = :userId")
    void deleteByUserId(Long userId);
}
