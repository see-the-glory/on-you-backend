package stg.onyou.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stg.onyou.model.entity.Club;
import stg.onyou.model.network.request.ClubSearchRequest;

import java.util.List;

@Primary
public interface ClubRepository extends JpaRepository<Club,Long>, ClubQRepository {

    List<Club> findAllByOrderByIdDesc(Pageable page);

    List<Club> findByIdLessThanOrderByIdDesc(Long id, Pageable page);

    Boolean existsByIdLessThan(Long id);

//    List<Club> findByCategory1IdOrCategory2IdOrderByIdDesc(Long category1Id, Long category2Id, Pageable page);
//    List<Club> findByCategory1IdAndCategory2IdOrderByIdDesc(Long category1Id, Long category2Id, Pageable page);
//
//    List<Club> findByCategory1IdAndCategory2IdLessThanOrderByIdDesc(Long category1Id, Long category2Id, Long id, Pageable page);
//    List<Club> findByCategory1IdOrCategory2IdAndIdLessThanOrderByIdDesc(Long category1Id, Long category2Id, Long id,  Pageable page);

//    List<Club> findByNameContainingOrShortDescContainingOrLongDescContaining(String name, String shortDesc, String longDesc);

//    @Query("SELECT c FROM Club c WHERE (:name is null or c.name like :name) or (:shortDesc is null or c.shortDesc like :shortDesc) or (:longDesc is null or c.longDesc like :longDesc)")
//    List<Club> findByNameAndDesc(@Param("name") String name, @Param("shortDesc") String shortDesc, @Param("longDesc") String longDesc);

    @Query("SELECT c FROM Club c " +
            "WHERE (:name is null or c.name like :name) " +
            "or (:shortDesc is null or c.shortDesc like :shortDesc)" +
            "or (:longDesc is null or c.longDesc like :longDesc)"
    )
    List<Club> findByNameAndDesc(@Param("name") String name, @Param("shortDesc") String shortDesc, @Param("longDesc") String longDesc);

}
