package stg.onyou.model.network.request;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Builder
@Data
public class ClubCondition {
    private String orderBy;
    private int showRecruitingOnly;
    private int showMy;
    private int min;
    private int max;
}
