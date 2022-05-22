package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stg.onyou.model.network.Header;
import stg.onyou.service.AwsS3Service;

import java.io.IOException;

@Api(tags = {"File download Controller - 파일 다운로드"})
@Slf4j
@RestController
@RequestMapping("/api/download")
public class FileDownloadController {

    @Autowired
    private AwsS3Service awsS3Service;

    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> download(@PathVariable String fileName) throws IOException {

        return awsS3Service.downloadFile(fileName);
    }
}
