package stg.onyou.model.network.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.InterestCategory;
import stg.onyou.model.entity.Interest;
import stg.onyou.model.entity.Organization;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    private String organizationName;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private char sex;
    private String birthday;
    private List<InterestCategory> interests;
}
