package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.UserApiRequest;
import stg.onyou.model.network.response.UserApiResponse;
import stg.onyou.service.UserApiService;

@Api(tags = {"User API Controller"})
@Slf4j
@RestController
@RequestMapping("/ap")
public class UserController {

    @Autowired
    private UserApiService userApiService;

        @GetMapping("/user/{id}")
        public Header<UserApiResponse> read(@PathVariable Long id){
            return userApiService.read(id);
        }

}
