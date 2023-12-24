package stg.onyou.model.network.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponse {

    private Long id;
    private Long userId;
    private String content;
    private String imageUrl;
    private boolean likeYn;
    private Long likesCount;
    private Long commentCount;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String customCursor;

    @QueryProjection
    public BoardResponse(Long id, Long userId, String content, LocalDateTime created, LocalDateTime updated, String customCursor)
    {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.created = created;
        this.updated = updated;
        this.customCursor = customCursor;
    }
}
