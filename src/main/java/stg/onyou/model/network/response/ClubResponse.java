package stg.onyou.model.network.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.enums.RecruitStatus;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClubResponse {

    private Long id;
    private String name;
    private String clubShortDesc;
    private String clubLongDesc;
    private String organizationName;
    private List<UserResponse> members;
    private List<CategoryResponse> categories;
    private int maxNumber;
    private int recruitNumber;
    private int feedNumber;
    private String thumbnail;
    private RecruitStatus recruitStatus; //BEGIN, RECRUIT, CLOSED
    private String isApprovedRequired; // "Y", "N"
    private String contactPhone;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime created;

}

