package stg.onyou.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.UserApiRequest;
import stg.onyou.model.network.response.UserApiResponse;
import stg.onyou.service.UserApiService;

@Slf4j
@RestController
@RequestMapping("/api/user") // client CALLS http://{serverip}:8080/api/user
public class UserController {

    @Autowired
    private UserApiService userApiService;

        @GetMapping("{id}") //client CALLS http://{serverip}:8080/api/user/{id}
        public Header<UserApiResponse> read(@PathVariable Long id){
            return userApiService.read(id);
        } //return json file ( user4 data )
    { key : value }
    { id: 4, name : 'soobeen', age:33 }

}
