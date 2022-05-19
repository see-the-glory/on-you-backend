package stg.onyou.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import stg.onyou.model.entity.Feed;
import stg.onyou.model.entity.FeedImage;
import stg.onyou.repository.FeedImageRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final FeedImageRepository feedImageRepository;

    public void uploadFile(List<MultipartFile> multipartFile, Feed feed, Long userId) {
//        List<FeedImage> feedImages = feed.getFeedImages();
        multipartFile.forEach(file -> {

            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            String key = "Feed/" + userId.toString() + "/" + fileName;
            try (InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }
//            FeedImage feedImage = FeedImage.builder().url(key).build();
//            feedImages.add(feedImage);
//            feedImageRepository.save(feedImage);
        });
    }

    public Resource getFile(Long userId, String storedFileName) throws IOException {
        S3Object o = amazonS3.getObject(new GetObjectRequest(bucket, "Feed/" + userId.toString() + "/" + storedFileName));
        S3ObjectInputStream objectInputStream = o.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);
        Resource resource = new ByteArrayResource(bytes);
        return resource;
    }

//    public void deleteFile(List<FeedImage> feedImages) {
//        feedImages.forEach(feedImage -> {
//            amazonS3.deleteObject(new DeleteObjectRequest(bucket, feedImage.getUrl()));
//            feedImageRepository.delete(feedImage);
//        });
//    }

    public void deleteFile(Long userId, String storedFileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, "Feed/" + userId.toString() + "/" + storedFileName));
    }


    private String createFileName(String fileName) { // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌립니다.
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) { // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며, 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단하였습니다.
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }
}
