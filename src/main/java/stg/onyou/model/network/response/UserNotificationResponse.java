package stg.onyou.model.network.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.ActionType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotificationResponse {

    private Long actionId;
    private Long actionerId;
    private String actionerName;
    private Long actioneeId;
    private String actioneeName;
    private Long actionClubId;
    private String actionClubName;
    private ActionType actionType;
    private String applyMessage;
    private boolean isProcessDone;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime created;

    @QueryProjection
    public UserNotificationResponse(Long actionId, Long actionerId, Long actioneeId, Long actionClubId, ActionType actionType, String applyMessage, boolean isProcessDone, LocalDateTime created){
        this.actionId = actionId;
        this.actionerId = actionerId;
        this.actioneeId = actioneeId;
        this.actionClubId = actionClubId;
        this.actionType = actionType;
        this.applyMessage = applyMessage;
        this.isProcessDone = isProcessDone;
        this.created = created;
    }
}
