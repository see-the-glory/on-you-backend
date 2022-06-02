package stg.onyou.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.Content;

import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<Club,Long> {

    List<Club> findAllByOrderByIdDesc(Pageable page);
    List<Club> findByIdLessThanOrderByIdDesc(Long id, Pageable page);
    Boolean existsByIdLessThan(Long id);
}
