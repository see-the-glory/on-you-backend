package stg.onyou.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import stg.onyou.model.network.FcmMessage;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

    private static final String API_URL = "https://fcm.googleapis.com/v1/projects/onyou-d9114/messages:send";
    private final ObjectMapper objectMapper;

    public void sendMessageTo(String targetToken, String title, String body, Long clubId) throws IOException {
        String message = makeMessage(targetToken, title, body, clubId);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }

    private String makeMessage(String targetToken, String title, String body, Long clubId) throws JsonParseException, JsonProcessingException {

        FcmMessage.Data data = FcmMessage.Data.builder()
                .clubId(clubId)
                .build();

        FcmMessage.Notification notification = FcmMessage.Notification.builder()
                .title(title)
                .body(body)
                .image(null)
                .build();

        FcmMessage.Message message = FcmMessage.Message.builder()
                .token(targetToken)
                .notification(notification)
                .data(data)
                .build();

        FcmMessage fcmMessage = FcmMessage.builder()
                .message(message)
                .validateOnly(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessage);
    }



    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/firebase_service_key.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}