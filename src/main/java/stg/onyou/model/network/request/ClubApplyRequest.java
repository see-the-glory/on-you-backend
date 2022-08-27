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
    private String memo;
    @NotNull
    @Positive
    private Long clubId;
}
