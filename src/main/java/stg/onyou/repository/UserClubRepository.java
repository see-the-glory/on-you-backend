package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.Feed;
import stg.onyou.model.entity.User;
import stg.onyou.model.entity.UserClub;

import java.util.List;
import java.util.Optional;

public interface UserClubRepository extends JpaRepository<UserClub, Long> {
    List<UserClub> findAllByClubId(Long id);
    Optional<UserClub> findByUserAndClub(User user, Club club);

    @Query("delete from UserClub uc where uc.user.id = :userId")
    void deleteByUserId(Long userId);

    @Query("select uc from UserClub uc where uc.user.id = :userId")
    List<UserClub> findByUserId(Long userId);


    @Query("select uc from UserClub uc where uc.club.id = :clubId and uc.role = 'MANAGER' order by uc.id desc")
    Optional<UserClub> findLatestManager(Long clubId);

    @Query("select uc from UserClub uc where uc.club.id = :clubId and uc.role = 'MANAGER' order by uc.id desc")
    Optional<UserClub> findLatestMember(Long clubId);

}
