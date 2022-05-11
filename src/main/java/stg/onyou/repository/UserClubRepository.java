package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.UserClub;

import java.util.List;
import java.util.Optional;

public interface UserClubRepository extends JpaRepository<UserClub, Long> {
    List<UserClub> findAllByClubId(Long id);
    Optional<UserClub> findByUserIdAndClubId(Long userId, Long clubId);
}
