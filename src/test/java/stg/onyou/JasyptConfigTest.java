package stg.onyou;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

@SpringBootTest
public class JasyptConfigTest {

    @Test
    public void jasyptTest(){
        //given
        String accessKey = "AKIAQDIAFRH6CI3WIJPC";
        String secretKey = "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";

        StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
        String encryptKey = System.getenv("JASYPT_PASS");
        System.out.println("password :"+encryptKey);
        jasypt.setPassword("Jun@6127");
        jasypt.setAlgorithm("PBEWithMD5AndDES");

        String encryptedAccessKey = jasypt.encrypt(accessKey);
        System.out.println("accessKey 암호화 내용: " + encryptedAccessKey);
        String decryptedAccessKey =  jasypt.decrypt(encryptedAccessKey);
        System.out.println("accessKey 복호화 내용: " + decryptedAccessKey);

        String encryptedSecretKey = jasypt.encrypt(secretKey);
        System.out.println("secretKey 암호화 내용: " + encryptedSecretKey);
        String decryptSecretKey =  jasypt.decrypt(encryptedSecretKey);
        System.out.println("accessKey 복호화 내용: " + decryptSecretKey);

        assertThat(accessKey, is(equalTo(decryptedAccessKey)));
        assertThat(secretKey, is(equalTo(decryptSecretKey)));

    }

}
