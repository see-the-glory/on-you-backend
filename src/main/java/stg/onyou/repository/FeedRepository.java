package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stg.onyou.model.entity.Feed;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    @Query("select f from Feed f where f.delYn = 'n' and f.reportCount < 5 and f.access = 'PUBLIC' order by f.created desc")
    List<Feed> findAll();

    @Query("select f from Feed f where f.delYn = 'n' and f.reportCount < 5 and f.content like concat('%', :content, '%') " +
            "or f.content like concat('%', '#', :hashtag, '%') order by f.created desc")
    List<Feed> findAllString(@Param("content") String content, @Param("hashtag") String hashtag);
}
