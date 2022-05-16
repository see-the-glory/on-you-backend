package stg.onyou.config.auth;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
import stg.onyou.model.entity.Organization;
import stg.onyou.model.entity.User;
import stg.onyou.repository.UserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.net.HttpURLConnection;
import java.net.URL;


@Service
@RequiredArgsConstructor
@Slf4j
public class LoadUserService {

    private final RestTemplate restTemplate = new RestTemplate();
    ParameterizedTypeReference<Map<String, Object>> RESPONSE_TYPE  =  new ParameterizedTypeReference<>(){};

    public PrincipalDetails getOAuth2UserDetails(AccessTokenKakao authentication) {

        //kakao에 request 날려서 사용자 정보 받아와서 kakaoResponse 타입으로 저장
        KakaoResponse kakaoResponse = sendRequestToKakao(authentication.getAccessToken());

        return PrincipalDetails.builder()
                .socialId(kakaoResponse.getSocialId()) //sendRequestToKakao에 request보내서 response로 socialId 리턴
                .birthday(kakaoResponse.getBirthday())
                .name(kakaoResponse.getName())
                .sex(kakaoResponse.getSex())
                .email(kakaoResponse.getEmail())
                .thumbnail(kakaoResponse.getThumbnail())
                .build();
    }

    private KakaoResponse sendRequestToKakao(String token){

        String requestURL = "https://kapi.kakao.com/v2/user/me";
        JsonElement kakaoUserInfo = null;
        KakaoResponse kakaoResponse = new KakaoResponse();

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod(HttpMethod.POST.name());
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            //Gson 라이브러리로 JSON파싱
            kakaoUserInfo = JsonParser.parseString(result);

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //kakao socialId 저장
        kakaoResponse.setSocialId(kakaoUserInfo.getAsJsonObject().get("id").getAsString());
        boolean hasEmail = kakaoUserInfo.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
        //email이 있는 사용자라면 kakao email 저
        if (hasEmail) {
            kakaoResponse.setEmail(kakaoUserInfo.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString());
        }

        kakaoResponse.setBirthday(kakaoUserInfo.getAsJsonObject().get("kakao_account").getAsJsonObject().get("birthday").getAsString());
        kakaoResponse.setThumbnail(kakaoUserInfo.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("thumbnail_image_url").getAsString());
        kakaoResponse.setName(kakaoUserInfo.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("nickname").getAsString());

        if(kakaoUserInfo.getAsJsonObject().get("kakao_account").getAsJsonObject().get("gender").getAsString().equals("male")){
            kakaoResponse.setSex('M');
        }
        else if(kakaoUserInfo.getAsJsonObject().get("kakao_account").getAsJsonObject().get("gender").getAsString().equals("female")){
            kakaoResponse.setSex('F');
        }

        return kakaoResponse;
//        try{
//            ResponseEntity<Map<String, Object>> response = restTemplate.exchange("https://kapi.kakao.com/v2/user/me",
//                    HttpMethod.GET,
//                    request,
//                    RESPONSE_TYPE);
//
//            System.out.println("id : "+response.getBody().get("id").toString() );
//
//            System.out.println(response.getBody());
//
//            JsonParser.parseReader(response.getBody());
//
//
//           return response.getBody().get("id").toString();//카카오는 id를 PK로 사용
//
//        } catch (Exception e) {
//            log.error("AccessToken을 사용하여 KAKAO 유저정보를 받아오던 중 예외가 발생했습니다 {}" ,e.getMessage() );
//            throw e;
//        }
    }

}
