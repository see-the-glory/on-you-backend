package stg.onyou.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.GuestComment;
import java.util.List;

public interface GuestCommentRepository extends JpaRepository<GuestComment, Long> {
    List<GuestComment> findByClub(Club club);
}