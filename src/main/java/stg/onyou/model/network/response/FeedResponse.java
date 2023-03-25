package stg.onyou.model.network.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedResponse {

    private Long id;
    private Long clubId;
    private String clubName;
    private Long userId;
    private String userName;
    private String content;
    private List<String> imageUrls;
    private List<String> hashtags;
    private boolean likeYn;
    private Long likesCount;
    private Long commentCount;
    private String thumbnail;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime created;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updated;
    private String customCursor;
    private List<LikeUserResponse> likeUserList;

    @QueryProjection
    public FeedResponse(Long id, Long clubId, String clubName, Long userId, String userName, String content,
                        String thumbnail, LocalDateTime created, LocalDateTime updated, String customCursor)
    {
        this.id = id;
        this.clubId = clubId;
        this.clubName = clubName;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.thumbnail = thumbnail;
        this.created = created;
        this.updated = updated;
        this.customCursor = customCursor;
    }
}
