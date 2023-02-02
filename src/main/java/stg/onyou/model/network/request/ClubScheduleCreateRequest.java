package stg.onyou.model.network.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubScheduleCreateRequest {

    @NotNull
    private Long clubId;
    @NotEmpty
    @Size(max = 20, message = "모임 명을 20자 이내로 입력해주세요.")
    private String name;
    @NotEmpty
    private String location;
    @NotEmpty
    @Size(max = 255, message = "내용을 255자 이내로 입력해주세요.")
    private String content;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endDate;

}
