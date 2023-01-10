package stg.onyou.model.network.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.ApplyStatus;
import stg.onyou.model.InterestCategory;
import stg.onyou.model.Role;
import stg.onyou.model.entity.Interest;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime created;
    private Role role;
    private String phoneNumber;
    private List<InterestCategory> interests;

    @QueryProjection
    public UserResponse(Long id, String organizationName, String thumbnail, String name, String birthday,
                        ApplyStatus applyStatus, char sex, String email, LocalDateTime created, Role role, String phoneNumber){

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
        this.phoneNumber = phoneNumber;
    }
}


