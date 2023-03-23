package stg.onyou.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.enums.AlarmType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PushAlarmResponse {
    private char userPushAlarm;
    private char clubPushAlarm;
}
