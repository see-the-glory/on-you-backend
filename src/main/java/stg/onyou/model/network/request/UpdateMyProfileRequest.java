package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateMyProfileRequest {

    private String about;
    private String isEmailPublic;
    private String isContactPublic;
    private String isBirthdayPublic;
    private String isClubPublic;
    private String isFeedPublic;
}
