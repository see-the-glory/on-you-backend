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
public class UserNotificationResponse {

    private Long actionerId;
    private String actionerName;
    private Long actioneeId;
    private String actioneeName;
    private Long actionClubId;
    private String actionClubName;
    private ActionType actionType;
    private String applyMessage;

    @QueryProjection
    public UserNotificationResponse(Long actionerId, Long actioneeId, Long actionClubId, ActionType actionType, String applyMessage){
        this.actionerId = actionerId;
        this.actioneeId = actioneeId;
        this.actionClubId = actionClubId;
        this.actionType = actionType;
        this.applyMessage = applyMessage;
    }
}
