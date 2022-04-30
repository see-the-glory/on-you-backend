package stg.onyou.model.network.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserApiResponse {

    private Long id;
    private String organizationName;
    private String name;
    private LocalDateTime birthdate;
    private char sex;
    private String accountEmail;
    private LocalDateTime created;

}
