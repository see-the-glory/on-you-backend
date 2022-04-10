package stg.onyou.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpMethod;
import stg.onyou.model.entity.Organization;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.response.UserApiResponse;
import stg.onyou.repository.UserRepository;
import stg.onyou.model.entity.User;

import java.time.LocalDateTime;

import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class UserApiService {

    @Autowired
    private UserRepository userRepository;

    public Header<UserApiResponse> selectUser(int id){

        return userRepository.findById(id)
                .map(user -> Header.OK(selectUserResponse(user)))
                .orElseThrow(
                        ()-> new CustomException(ErrorCode.USER_NOT_FOUND)
                );
    }

    private UserApiResponse selectUserResponse(User user){

        UserApiResponse userApiResponse = UserApiResponse.builder()
                .id(user.getId())
                .organizationName(user.getOrganization().getName())
                .birthdate(user.getBirthdate())
                .sex(user.getSex())
                .accountEmail(user.getAccountEmail())
                .created(user.getCreated())
                .build();

        return userApiResponse;

    }

    public String getKaKaoAccessToken(String code) throws CustomException {
        String accessToken = "";
        String requestURL = "https://kauth.kakao.com/oauth/token";

        try{
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod(HttpMethod.POST.name());
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=e274b862274fefe84b97be1c7383d43e");
            sb.append("&redirect_uri=http://13.125.93.119:8080/api/user/kakao");
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            conn.getResponseCode();
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonElement element = JsonParser.parseString(result);

            accessToken = element.getAsJsonObject().get("access_token").getAsString();

            br.close();
            bw.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return accessToken;
    }

    public JsonElement getKakaoUser(String token) throws CustomException {

        String requestURL = "https://kapi.kakao.com/v2/user/me";
        JsonElement element = null;

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
            element = JsonParser.parseString(result);

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return element;
    }

    public User join(JsonElement kakaoUserInfo) throws CustomException {

        // int id = kakaoUserInfo.getAsJsonObject().get("id").getAsInt();
        // 위 코드는 나중에 사용 가능성이 있어 남겨둠
        String email = "";
        boolean hasEmail = kakaoUserInfo.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
        if (hasEmail) {
            email = kakaoUserInfo.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
        }

        // db를 조회해서 이미 있는 사용자라면, 사용자 정보를 리턴한다 (사용자를 조회하는 jpa 생성하야함 유저레파지토리에서)
        // 조회결과를 if문으로 있는지 없는지 확인 있으면 조회결과 리턴 없으면 유저를 만들어서 밑에있는 코드를 리턴하면 됨
        User user = userRepository.findByAccountEmail(email);
        if (user == null){
            Organization organization = new Organization();
            organization.setId(1);

            user = new User();
            user.setAccountEmail(email);
            user.setCreated(LocalDateTime.now());
            user.setOrganization(organization);
            user.setName("오수빈");
            user.setSex('F'); // to do enum
            user = userRepository.save(user);
        }
        return user;
    }

}