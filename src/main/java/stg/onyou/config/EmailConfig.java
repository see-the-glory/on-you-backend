package stg.onyou.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class EmailConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public EmailConfig() throws IOException {
        logger.info("EmailConfig.java constructor called");
    }

    @Value("${mail.transport.protocol}")
    private String protocol;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean auth;

    @Value("${spring.mail.properties.mail.smtp.ssl.enable}")
    private boolean starttls;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", auth);
        properties.put("mail.smtp.starttls.enable", starttls);
        properties.put("mail.transport.protocol", protocol);

        StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();

        String jasyptPassword = System.getProperty("jasypt_password");
        jasypt.setPassword(jasyptPassword);
        jasypt.setAlgorithm("PBEWithMD5AndDES");

        String decryptUsername = jasypt.decrypt(username);
        String decryptPassword = jasypt.decrypt(password);

        mailSender.setHost(host);
        mailSender.setUsername(decryptUsername);
        mailSender.setPassword(decryptPassword);
        mailSender.setPort(port);
        mailSender.setJavaMailProperties(properties);

        return mailSender;
    }
}
