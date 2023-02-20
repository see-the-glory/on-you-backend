package stg.onyou.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.User;
import stg.onyou.model.network.request.ClubSearchRequest;

import java.util.List;
import java.util.Optional;

@Primary
public interface ClubRepository extends JpaRepository<Club,Long>, ClubQRepository {

    Optional<Club> findByName(String clubName);
    Boolean existsByIdLessThan(Long id);

}
