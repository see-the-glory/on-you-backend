package stg.onyou.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Config {
    @Value("${cloud.aws.credentials.accesskey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretkey}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

//    @Bean
//    public AmazonS3Client amazonS3Client() {

//        StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
//
//        String jasyptPassword = System.getProperty("jasypt_password");
//        jasypt.setPassword(jasyptPassword);
//        jasypt.setAlgorithm("PBEWithMD5AndDES");
//
//        String decryptAccessKey = jasypt.decrypt(accessKey);
//        String decryptSecretKey = jasypt.decrypt(secretKey);
//
//        BasicAWSCredentials awsCreds = new BasicAWSCredentials(decryptAccessKey,decryptSecretKey);
//        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
//                .withRegion(region)
//                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
//                .build();
//    }
}
