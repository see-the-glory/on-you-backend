package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.UserClub;

import java.util.List;

public interface UserClubRepository extends JpaRepository<UserClub, Integer> {
    List<UserClub> findAllByClubId(Integer id);
}
