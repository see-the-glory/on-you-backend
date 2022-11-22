package stg.onyou.model.network.response;

import com.querydsl.core.annotations.QueryProjection;
import stg.onyou.model.ActionType;

public class UserNotificationResponse {

    private Long actionerId;
    private Long actioneeId;
    private Long actionClubId;
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
