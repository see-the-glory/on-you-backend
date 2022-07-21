package stg.onyou.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import stg.onyou.model.entity.Club;
import stg.onyou.model.network.request.ClubSearchRequest;


public interface ClubQRepository {

    Page<Club> findClubSearchList(Pageable page, ClubSearchRequest clubSearchRequest);

}
