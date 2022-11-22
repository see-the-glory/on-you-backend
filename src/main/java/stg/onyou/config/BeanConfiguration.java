package stg.onyou.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stg.onyou.repository.UserNotificationQRepository;
import stg.onyou.repository.UserNotificationQRepositoryImpl;

import com.querydsl.jpa.impl.JPAQueryFactory;

@Configuration
public class BeanConfiguration {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

}
