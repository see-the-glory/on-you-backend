package stg.onyou.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuestCommentResponse {
    private Long userId;
    private String userName;
    private String thumbnail;
    private String content;
    private LocalDateTime created;
}
