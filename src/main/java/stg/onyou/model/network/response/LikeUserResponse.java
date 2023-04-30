package stg.onyou.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LikeUserResponse {
        private Long userId;
        private String userName;
        private String thumbnail;
        private LocalDateTime likeDate;
}
