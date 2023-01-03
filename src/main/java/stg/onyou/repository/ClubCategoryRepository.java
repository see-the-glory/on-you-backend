package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import stg.onyou.model.entity.Category;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.ClubCategory;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ClubCategoryRepository extends JpaRepository<ClubCategory,Long> {
    List<ClubCategory> findByClub(Club club);

    @Transactional
    @Modifying
    @Query("DELETE FROM ClubCategory cc WHERE cc.club = :club")
    Integer deleteByClubId(Club club);
}
