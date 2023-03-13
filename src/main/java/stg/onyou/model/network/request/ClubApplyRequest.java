package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubApplyRequest {

    @NotEmpty
    @Size(max = 100, message = "가입 신청 내용은 100자 이내로 입력해주세요.")
    private String message;
    @NotNull
    @Positive
    private Long clubId;
}
