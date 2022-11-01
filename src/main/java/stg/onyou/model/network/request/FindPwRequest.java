package stg.onyou.model.network.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FindPwRequest {
    private String email;
    private String username;
    private String phoneNumber;
    private String birthday;
}
