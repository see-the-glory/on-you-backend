package stg.onyou.model.network.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class CheckEmailRequest {

    @NotEmpty
    private String email;
}
