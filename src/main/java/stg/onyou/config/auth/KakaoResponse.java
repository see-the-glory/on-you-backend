package stg.onyou.config.auth;

import lombok.Data;

@Data
public class KakaoResponse {

    private String socialId;
    private String email;
    private String name;
    private String thumbnail;
    private String birthday;
    private char sex;

}
