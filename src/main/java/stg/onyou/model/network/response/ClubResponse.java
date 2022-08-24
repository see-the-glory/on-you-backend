package stg.onyou.model.network.response;

import com.sun.istack.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;
import stg.onyou.model.RecruitStatus;

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
    private String thumbnail;
    private RecruitStatus recruitStatus; //BEGIN, RECRUIT, CLOSED
    private String isApprovedRequired; // "Y", "N"
    private String contactPhone;
    private String creatorName;
    private LocalDateTime created;

}

