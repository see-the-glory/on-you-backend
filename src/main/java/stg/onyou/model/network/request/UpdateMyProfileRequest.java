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
    private boolean isEmailPublic;
    private boolean isContactPublic;
    private boolean isBirthdayPublic;
    private boolean isClubPublic;
    private boolean isFeedPublic;
}
