package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stg.onyou.model.entity.Club;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.response.ClubApiResponse;
import stg.onyou.service.ClubApiService;

import java.util.List;

@Api(tags = {"Club API Controller"})
@Slf4j
@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    @Autowired
    private ClubApiService clubApiService;

    @GetMapping("/{id}")
    public Header<ClubApiResponse> selectClub(@PathVariable Integer id){
        return clubApiService.selectClub(id);
    }

    @GetMapping("")
    public Header<List<ClubApiResponse>> selectAllClubs(){
        return clubApiService.selectAllClubs();
    }
    /*
    @PostMapping("{id}/register")
    public*/
}
