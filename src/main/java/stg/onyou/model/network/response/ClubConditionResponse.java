package stg.onyou.model.network.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
    private String clubLongDesc;
    private String organizationName;
    private int maxNumber;
    private int recruitNumber;
    private String thumbnail;
    private RecruitStatus recruitStatus;
    private String creatorName;
    private LocalDateTime created;
    private List<UserResponse> members;
    private List<CategoryResponse> categories;
    private String customCursor;

    @QueryProjection
    public ClubConditionResponse(Long id, String name, String clubLongDesc, String clubShortDesc, String organizationName,
        int maxNumber, int recruitNumber, String thumbnail, RecruitStatus recruitStatus, String creatorName, LocalDateTime created, String customCursor){
        this.id = id;
        this.name = name;
        this.clubShortDesc = clubShortDesc;
        this.clubLongDesc = clubLongDesc;
        this.organizationName = organizationName;
        this.maxNumber = maxNumber;
        this.recruitNumber = recruitNumber;
        this.thumbnail = thumbnail;
        this.recruitStatus = recruitStatus;
        this.creatorName = creatorName;
        this.created = created;
        this.customCursor = customCursor;
    }

}
