package stg.onyou.model.network.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.ApplyStatus;
import stg.onyou.model.Role;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String organizationName;
    private String thumbnail;
    private String name;
    private String birthday;
    private ApplyStatus applyStatus;
    private char sex;
    private String email;
    private LocalDateTime created;
    private Role role;

    @QueryProjection
    public UserResponse(Long id, String organizationName, String thumbnail, String name, String birthday,
                        ApplyStatus applyStatus, char sex, String email, LocalDateTime created, Role role){

        this.id = id;
        this.organizationName = organizationName;
        this.thumbnail = thumbnail;
        this.name = name;
        this.birthday = birthday;
        this.applyStatus = applyStatus;
        this.sex = sex;
        this.email = email;
        this.created = created;
        this.role = role;
    }
}


