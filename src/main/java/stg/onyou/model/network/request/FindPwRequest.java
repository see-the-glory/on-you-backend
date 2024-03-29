package stg.onyou.model.network.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class FindPwRequest {

    @NotEmpty
    private String email;
    @NotEmpty
    private String username;
    @NotEmpty
    private String phoneNumber;
    @NotEmpty
    private String birthday;
}
