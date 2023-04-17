package stg.onyou.model.network.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.enums.ApplyStatus;
import stg.onyou.model.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageResponse {

    private String about;
    private String email;
    private boolean isEmailPublic;
    private String contact;
    private boolean isContactPublic;
    private LocalDate birthday;
    private boolean isBirthdayPublic;
    private List<ClubDTO> clubs;
    private boolean isClubPublic;
    private boolean isFeedPublic;
//    private List<FeedResponse> myFeedList;

    @Data
    @Builder
    public static class ClubDTO {
        private Long id;
        private String name;
        private Long recruitNumber;
        private List<CategoryResponse> categories;
        private Role role;
    }
}
