package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stg.onyou.model.entity.Club;

@Repository
public interface ClubRepository extends JpaRepository<Club,Integer> {
}
