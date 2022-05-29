package stg.onyou.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.ApplyStatus;
import stg.onyou.model.Role;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserClubResponse {

    private Long userId;
    private Long clubId;
    private ApplyStatus applyStatus;
    private LocalDateTime approveDate;
    private LocalDateTime applyDate;
    private Role role;
}
