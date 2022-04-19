package stg.onyou.config.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import stg.onyou.config.jwt.AccessTokenKakao;
import stg.onyou.model.entity.User;
import stg.onyou.repository.UserRepository;

import java.util.Collections;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class LoadUserService {

    private final RestTemplate restTemplate = new RestTemplate();
    ParameterizedTypeReference<Map<String, Object>> RESPONSE_TYPE  =  new ParameterizedTypeReference<>(){};

    public PrincipalDetails getOAuth2UserDetails(AccessTokenKakao authentication) {

        HttpHeaders headers = new HttpHeaders();
        setHeaders(authentication.getAccessToken(), headers);

        /*
        1. accesstoken 받은 것으
        2  Header setting
        3. request 날려서 social Id 리턴
        */
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);


        return PrincipalDetails.builder()
                .socialId(sendRequestToKakao(request)) //sendRequestToKakao에 request보내서 response로 socialId 리턴
                .build();

    }

    private void setHeaders(String accessToken, HttpHeaders headers) {
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    private String sendRequestToKakao(HttpEntity request){

        try{
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange("https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    request,
                    RESPONSE_TYPE);

            return response.getBody().get("id").toString();//카카오는 id를 PK로 사용

        } catch (Exception e) {
            log.error("AccessToken을 사용하여 KAKAO 유저정보를 받아오던 중 예외가 발생했습니다 {}" ,e.getMessage() );
            throw e;
        }
    }

}
