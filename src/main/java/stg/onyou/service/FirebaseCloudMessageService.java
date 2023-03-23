package stg.onyou.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import stg.onyou.model.network.FcmMessage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

    public Message makeMessage(String targetToken, String title, String body, MessageMetaData data) {

        Message msg = Message.builder()
                .setToken(targetToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .setImage(null)
                        .build()
                )
                .putData("clubId", Optional.ofNullable(String.valueOf(data.getClubId())).orElse(null))
                .putData("actionId", Optional.ofNullable(String.valueOf(data.getActionId())).orElse(null))
                .putData("feedId", Optional.ofNullable(String.valueOf(data.getFeedId())).orElse(null))
                .putData("commentId", Optional.ofNullable(String.valueOf(data.getCommentId())).orElse(null))
                .build();

        return msg;
    }
}

@Data
@Builder
class MessageMetaData {
    private Long clubId;
    private Long feedId;
    private Long commentId;
    private Long actionId;
}
