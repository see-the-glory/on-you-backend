package stg.onyou.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubRoleResponse {

    private Long userId;
    private Long clubId;
    private Role role;

}
