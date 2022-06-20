package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.FeedHashtag;

public interface FeedHashtagRepository extends JpaRepository<FeedHashtag, Long> {
}
