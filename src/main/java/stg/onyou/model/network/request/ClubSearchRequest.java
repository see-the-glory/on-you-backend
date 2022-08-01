package stg.onyou.model.network.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.RecruitStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubSearchRequest {

    //String orderBy; // ASC or DESC
    //String sortType; // CLUB_CREATED, MEMBER_NUM, FEED_NUM, LIKES_NUM

    RecruitStatus recruitStatus;
    Integer minMemberNum;
    Integer maxMemberNum;

}
