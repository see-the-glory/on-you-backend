package stg.onyou.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import stg.onyou.model.network.request.ClubCondition;
import stg.onyou.model.network.response.ClubConditionResponse;


public interface ClubQRepository {

    Page<ClubConditionResponse> findClubSearchList(Pageable page, ClubCondition clubCondition, String customCursor, Long userId, String keyword);

}
