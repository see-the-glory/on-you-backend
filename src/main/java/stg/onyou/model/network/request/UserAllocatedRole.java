package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAllocatedRole {

    private Long userId;
    private Role role;
}
