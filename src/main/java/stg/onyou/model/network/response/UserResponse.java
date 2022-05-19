package stg.onyou.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.ApplyStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String organizationName;
    private String name;
    private String birthday;
    private ApplyStatus applyStatus;
    private char sex;
    private String email;
    private LocalDateTime created;

}
