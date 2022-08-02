package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Category;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.ClubCategory;

import java.util.List;
import java.util.Optional;

public interface ClubCategoryRepository extends JpaRepository<ClubCategory,Long> {
    List<ClubCategory> findByClub(Club club);
}
