package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.network.response.FeedResponse;
import stg.onyou.model.network.response.MyPageResponse;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateMyPageRequest {

    private String about;
    private boolean isEmailPublic;
    private boolean isContactPublic;
    private boolean isBirthdayPublic;
    private List<UpdateMyPageRequest.ClubDTO> clubs;
    private boolean isFeedPublic;

    @Data
    public static class ClubDTO {
        private Long id;
        private boolean isPublic;
    }
}
