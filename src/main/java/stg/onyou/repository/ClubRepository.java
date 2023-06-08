package stg.onyou.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Club;

import java.util.Optional;

@Primary
public interface ClubRepository extends JpaRepository<Club,Long>, ClubQRepository {

    Optional<Club> findByName(String clubName);
    Boolean existsByIdLessThan(Long id);

}
