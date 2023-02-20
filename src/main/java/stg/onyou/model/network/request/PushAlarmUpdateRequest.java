package stg.onyou.model.network.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.AlarmType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushAlarmUpdateRequest {

    private AlarmType alarmType;
    private char isOnOff;
}
