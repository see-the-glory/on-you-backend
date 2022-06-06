package stg.onyou.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import stg.onyou.model.AccessModifier;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.Feed;
import stg.onyou.model.entity.FeedSearch;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedRepository {

    private final EntityManager em;

    public void save(Feed feed) {
        em.persist(feed);
    }

    public Feed findOne(Long id) {
        return em.find(Feed.class, id);
    }

    public List<Feed> findAll() {
        return em.createQuery("select f from Feed f where f.delYn = 'n' " +
                        "and f.reportCount < 5 " +
                        "and f.access = :access", Feed.class)
                .setParameter("access", AccessModifier.PUBLIC)
                .getResultList();
    }


    /*
     TODO
     QueryDSL 공부 후에 구현
     */

    public List<Feed> findAllString(FeedSearch feedSearch) {
        return em.createQuery("select f from Feed f where f.delYn = 'n' " +
                        "and f.content like :content", Feed.class)
                .setParameter("content", "%"+feedSearch.getContent()+"%")
                .getResultList();
    }

}
