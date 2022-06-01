package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stg.onyou.model.entity.Content;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.response.CategoryResponse;
import stg.onyou.repository.ContentRepository;
import stg.onyou.service.CategoryService;
import stg.onyou.service.ContentService;
import stg.onyou.service.CursorResult;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.List;

@Api(tags = {"Content API Controller"})
@Slf4j
@RestController
@RequestMapping("/api/contents")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping
    public Header<CursorResult<Content>>selectContentList(Long cursorId, Integer size) {

        if (size == null) size = 10;
        CursorResult<Content> contents = contentService.get(cursorId, PageRequest.of(0, size));

        return Header.OK(contents);

    }


}
