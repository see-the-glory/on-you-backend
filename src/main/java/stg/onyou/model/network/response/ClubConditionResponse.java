package stg.onyou.model.network.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.RecruitStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ClubConditionResponse {

    private Long id;
    private String name;
    private String clubShortDesc;
    private String organizationName;
    private int maxNumber;
    private int recruitNumber;
    private int feedNumber;
    private int clubLikesNumber;
    private String thumbnail;
    private RecruitStatus recruitStatus;
    private String isApprovedRequired;
    private LocalDateTime created;
    private List<UserResponse> members;
    private List<CategoryResponse> categories;
    private String contactPhone;
    private String customCursor;

    @QueryProjection
    public ClubConditionResponse(Long id, String name, String clubShortDesc, String organizationName,
                                 int maxNumber, int recruitNumber, int feedNumber, int clubLikesNumber, String thumbnail, RecruitStatus recruitStatus,
                                 String isApprovedRequired, LocalDateTime created, String contactPhone, String customCursor){

        this.id = id;
        this.name = name;
        this.clubShortDesc = clubShortDesc;
        this.organizationName = organizationName;
        this.maxNumber = maxNumber;
        this.recruitNumber = recruitNumber;
        this.feedNumber = feedNumber;
        this.clubLikesNumber = clubLikesNumber;
        this.thumbnail = thumbnail;
        this.recruitStatus = recruitStatus;
        this.isApprovedRequired = isApprovedRequired;
        this.created = created;
        this.customCursor = customCursor;
    }

}