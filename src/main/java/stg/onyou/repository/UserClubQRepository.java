package stg.onyou.repository;

import org.springframework.stereotype.Repository;
import stg.onyou.model.entity.UserClub;

import java.util.Optional;

@Repository
public interface UserClubQRepository {

    Optional<UserClub> findLatestManager(Long clubId);
    Optional<UserClub> findLatestMember(Long clubId);
}
