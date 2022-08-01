package stg.onyou.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import stg.onyou.model.entity.Club;
import stg.onyou.model.network.request.ClubSearchRequest;
import stg.onyou.model.network.response.ClubConditionResponse;


public interface ClubQRepository {

    Page<ClubConditionResponse> findClubSearchList(String clubCreatedCursor, Pageable page, ClubSearchRequest clubSearchRequest);

}
