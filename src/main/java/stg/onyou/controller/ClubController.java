package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.response.ClubApiResponse;
import stg.onyou.service.ClubApiService;

@Api(tags = {"Club API Controller"})
@Slf4j
@RestController
@RequestMapping("/api/club")
public class ClubController {

    @Autowired
    private ClubApiService clubApiService;

    @GetMapping("/{id}")
    public Header<ClubApiResponse> selectClub(@PathVariable Integer id){
        return clubApiService.selectClub(id);
    }
}
