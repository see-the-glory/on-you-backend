package stg.onyou.model.network.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.ActionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubNotificationResponse {

    private Long actionerId;
    private String actionerName;
    private Long actioneeId;
    private String actioneeName;
    private ActionType actionType;
    private String applyMessage;

    @QueryProjection
    public ClubNotificationResponse(Long actionerId, Long actioneeId, ActionType actionType, String applyMessage){
        this.actionerId = actionerId;
        this.actioneeId = actioneeId;
        this.actionType = actionType;
        this.applyMessage = applyMessage;
    }
}
