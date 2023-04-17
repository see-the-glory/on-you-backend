package stg.onyou.model.network.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.enums.ActionType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubNotificationResponse {

    private Long actionId;
    private Long actionerId;
    private String actionerName;
    private Long actioneeId;
    private String actioneeName;
    private ActionType actionType;
    private String message;
    private boolean isProcessDone;
    private boolean isRead;
    private boolean processDone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime created;

    @QueryProjection
    public ClubNotificationResponse(Long actionId, Long actionerId, Long actioneeId, ActionType actionType, String message, boolean isProcessDone, boolean isRead, boolean processDone, LocalDateTime created){
        this.actionId = actionId;
        this.actionerId = actionerId;
        this.actioneeId = actioneeId;
        this.actionType = actionType;
        this.message = message;
        this.isProcessDone = isProcessDone;
        this.isRead = isRead;
        this.processDone = isProcessDone;
        this.created = created;
    }
}
