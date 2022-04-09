package stg.onyou.controller;


import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.response.CategoryResponse;
import stg.onyou.service.CategoryApiService;

@Api(tags = {"Category API Controller"})
@Slf4j
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryApiService categoryApiService;

    @GetMapping("")
    public Header<CategoryResponse> selectAllCategories(){
        return categoryApiService.selectAllCategories;
    }


}
