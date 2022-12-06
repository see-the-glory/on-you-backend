package stg.onyou.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import stg.onyou.model.entity.Feed;
import stg.onyou.model.network.response.FeedResponse;
import stg.onyou.repository.FeedQRepository;
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
