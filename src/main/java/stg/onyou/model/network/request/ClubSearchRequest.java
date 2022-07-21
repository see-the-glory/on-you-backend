package stg.onyou.model.network.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubSearchRequest {

    String orderBy;
    String sortType; // CLUB_CREATED, MEMBER_NUM, FEED_NUM, LIKES_NUM
}
