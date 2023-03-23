package stg.onyou.model.network.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    private String organizationName;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String sex;
    private String birthday;

}
