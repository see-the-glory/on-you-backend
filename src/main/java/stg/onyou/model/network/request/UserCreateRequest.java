package stg.onyou.model.network.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.entity.Organization;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    private Organization organization;
    private String birthday;
    private String thumbnail;
    private String email;
}
