package stg.onyou.controller;


import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stg.onyou.model.entity.Content;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.response.CategoryResponse;
import stg.onyou.repository.ContentRepository;
import stg.onyou.service.CategoryService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = {"Content API Controller"})
@Slf4j
@RestController
@RequestMapping("/api/contents")
public class ContentController {

    @Autowired
    private ContentRepository contentRepository;

    @GetMapping("")
    public Page<Content> selectAllCategories(HttpServletRequest httpServletRequest){


        return contentRepository.findAll(PageRequest.of(0,3));
    }


}
