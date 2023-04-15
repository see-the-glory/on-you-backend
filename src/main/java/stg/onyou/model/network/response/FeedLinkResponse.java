package stg.onyou.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedLinkResponse {

    private Long clubId;
    private String clubName;
    private Long userId;
    private String userName;
    private String content;
    private List<String> imageUrls;
    private boolean likeYn;
    private Long likesCount;
    private Long commentCount;
    private String thumbnail;
    private List<LikeUserResponse> likeUserList;
    private List<CommentResponse> commentList;
    private LocalDateTime created;
    private LocalDateTime updated;
}
