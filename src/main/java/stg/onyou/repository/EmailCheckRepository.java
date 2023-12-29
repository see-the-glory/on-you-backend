package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.ClubCategory;
import stg.onyou.model.entity.Comment;
import stg.onyou.model.entity.EmailCheck;

import java.util.List;
import java.util.Optional;

public interface EmailCheckRepository extends JpaRepository<EmailCheck, Long> {

    Optional<EmailCheck> findByEmail(String email);
}
