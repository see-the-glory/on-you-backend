package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stg.onyou.model.entity.Feed;

import java.util.List;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {

    List<Feed> findAllByAccess(String access);
    List<Feed> findAllByAccessOrderByCreatedDesc(String access);
    List<Feed> findAllByTitleContainingOrContentContaining(String title, String content);


}
