package stg.onyou.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.ApplyStatus;
import stg.onyou.model.RecruitStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyClubResponse {

    private Long id;
    private String name;
    private String clubShortDesc;
    private String clubLongDesc;
    private String organizationName;
    private List<CategoryResponse> categories;
    private String thumbnail;
    private ApplyStatus applyStatus; // APPLIED, APPROVED
    private String creatorName;
    private LocalDateTime created;

}

