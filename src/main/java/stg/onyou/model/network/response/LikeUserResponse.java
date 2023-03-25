package stg.onyou.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LikeUserResponse {
        private String thumbnail;
        private String userName;
        private LocalDateTime likeDate;
}
