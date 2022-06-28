package stg.onyou.model.network.response;

import com.sun.istack.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.RecruitStatus;

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
    private int maxNumber;
    private int recruitNumber;
    private String thumbnail;
    private RecruitStatus recruitStatus; //BEGIN, RECRUIT, CLOSED
    private String creatorName;
    private String category1Name;
    private String category2Name;
}

