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

    public Header<UserApiResponse> selectUser(Long id){

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
                .birthday(user.getBirthday())
                .sex(user.getSex())
                .email(user.getEmail())
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


}