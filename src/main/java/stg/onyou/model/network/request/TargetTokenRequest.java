package stg.onyou.model.network.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetTokenRequest {

    @NotEmpty
    private String targetToken;
}
