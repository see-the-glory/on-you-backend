package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"Test API Controller"})
@Slf4j
@RestController
public class TestController {

    @PostMapping("/test")
    public String test() {
        return "test success!";
    }
}
