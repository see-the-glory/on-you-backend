package stg.onyou.config;

import javax.persistence.EntityManager;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;


@Configuration
public class JpaQueryFactory {

    @Bean
    public JPAQueryFactory queryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

}