package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.FeedImage;

public interface FeedImageRepository extends JpaRepository<FeedImage,Long> {
}
