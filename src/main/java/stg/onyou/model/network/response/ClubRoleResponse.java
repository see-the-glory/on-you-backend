package stg.onyou.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.enums.ApplyStatus;
import stg.onyou.model.enums.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubRoleResponse {

    private Long userId;
    private Long clubId;
    private Role role;
    private ApplyStatus applyStatus;

}
