package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubRejectRequest {

    @NotNull
    @Positive
    private Long userId;
    @NotNull
    @Positive
    private Long clubId;
    @NotNull
    @Positive
    private Long actionId;
    private String message;
}
