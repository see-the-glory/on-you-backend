package stg.onyou.model.network.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class CheckStringRequest {

    @NotEmpty
    private String email;
    @NotEmpty
    private String checkString;
}
