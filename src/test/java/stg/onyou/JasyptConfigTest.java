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
        String accessKey = "AKIAQDIAFRH6CHXKUOKK";
        String secretKey = "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";

        StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
        jasypt.setPassword("Jun@6127");
        jasypt.setAlgorithm("PBEWithMD5AndDES");

        String encryptedAccessKey = jasypt.encrypt(accessKey);
        System.out.println("accessKey 암호화 내용: " + encryptedAccessKey);
        String decryptedAccessKey =  jasypt.decrypt(encryptedAccessKey);

        String encryptedSecretKey = jasypt.encrypt(secretKey);
        System.out.println("secretKey 암호화 내용: " + encryptedSecretKey);
        String decryptSecretKey =  jasypt.decrypt(encryptedSecretKey);

        assertThat(accessKey, is(equalTo(decryptedAccessKey)));
        assertThat(secretKey, is(equalTo(decryptSecretKey)));

    }

}
