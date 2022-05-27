package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubScheduleUpdateRequest {

    @Size(max = 20, message = "모임 명을 20자 이내로 입력해주세요.")
    private String name;
    private String location;
    @Size(max = 255, message = "내용을 255자 이내로 입력해주세요.")
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
