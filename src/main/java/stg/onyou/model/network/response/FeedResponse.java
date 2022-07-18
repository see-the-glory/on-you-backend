package stg.onyou.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedResponse {

    Long feedId;
    Long clubId;
    String clubName;
    Long userId;
    String userName;
    String content;
    List<String> imageUrls;
    List<String> hashtags;
    boolean likeYn;
    int likesCount;
    int commentCount;


}
