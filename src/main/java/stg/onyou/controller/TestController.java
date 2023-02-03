package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import stg.onyou.service.FirebaseCloudMessageService;

import java.io.IOException;

@Api(tags = {"Test API Controller"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final FirebaseCloudMessageService firebaseCloudMessageService;

    @PostMapping("/api/test/fcm")
    public ResponseEntity pushMessage(@RequestBody TestDTO requestDTO) throws IOException {

        System.out.println(requestDTO.getTargetToken() + " "
                +requestDTO.getTitle() + " " + requestDTO.getBody());

        firebaseCloudMessageService.sendMessageTo(
                requestDTO.getTargetToken(),
                requestDTO.getTitle(),
                requestDTO.getBody());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/test")
    public String test() {
        return "test success!";
    }


}
