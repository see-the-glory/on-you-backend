package stg.onyou.model.network.response;

import com.querydsl.core.annotations.QueryProjection;
import stg.onyou.model.ActionType;

public class ClubNotificationResponse {

    private Long actionerId;
    private Long actioneeId;
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
