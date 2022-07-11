package stg.onyou.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.ClubLikes;
import stg.onyou.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface ClubLikesRepository extends JpaRepository<ClubLikes, Long> {

    Optional<ClubLikes> findByClubAndUser(Club club, User user);
}
