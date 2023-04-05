//package stg.onyou.controller;
//
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingException;
//import io.swagger.annotations.Api;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//import stg.onyou.model.enums.ActionType;
//import stg.onyou.model.network.FcmMessage;
//import stg.onyou.model.network.MessageMetaData;
//import stg.onyou.service.FirebaseCloudMessageService;
//import com.google.firebase.messaging.Message;
//
//import java.io.IOException;
//
//@Api(tags = {"Test API Controller"})
//@Slf4j
//@RestController
//public class TestController {
//
//    private final FirebaseMessaging fcm;
//    private final FirebaseCloudMessageService firebaseCloudMessageService;
//
//    public TestController(FirebaseMessaging fcm, FirebaseCloudMessageService firebaseCloudMessageService) {
//        this.fcm = fcm;
//        this.firebaseCloudMessageService = firebaseCloudMessageService;
//    }
//    @PostMapping("/api/test/fcm")
//    public ResponseEntity pushMessage(@RequestBody TestDTO requestDTO) throws IOException, FirebaseMessagingException {
//
//        MessageMetaData data = MessageMetaData.builder()
//                .type(ActionType.APPLY)
//                .build();
//
//        Message fcmMessage = firebaseCloudMessageService.makeMessage(
//                requestDTO.getTargetToken(),
//                requestDTO.getTitle(),
//                requestDTO.getBody(),
//                data
//        );
//
//        String id = fcm.send(fcmMessage);
//
//        return ResponseEntity
//                .status(HttpStatus.ACCEPTED)
//                .body(id);
//    }
//
//    @PostMapping("/test")
//    public String test() {
//        return "test success!";
//    }
//
//
//}
