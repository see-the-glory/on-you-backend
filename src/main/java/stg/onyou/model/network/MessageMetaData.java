package stg.onyou.model.network;

import lombok.Builder;
import lombok.Data;
import stg.onyou.model.enums.ActionType;

@Data
@Builder
public class MessageMetaData {
    private ActionType type;
    private Long clubId;
    private Long feedId;
    private Long commentId;
    private Long actionId;
}
