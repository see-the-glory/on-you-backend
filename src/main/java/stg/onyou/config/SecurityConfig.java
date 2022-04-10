package stg.onyou.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // prepost로 권한 체크를 하겠다는 의미이다.
public class SecurityConfig extends WebSecurityConfigurerAdapter { //이 클래스가 filter chain을 구성햐는 config 클래스이다.


    /* configure 메소드에서 설정하는 것이 filter 프록시의 실제 filter들을 셋팅하는 역할을 함.
    url 패턴에 따라 다른 필터를 적용할 수 있으며, web resource의 경우는 ignore하는 것도 가능함 */
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
            .cors().disable()
            .csrf().disable()
            .formLogin().disable()
            .headers().frameOptions().disable(); //iframe 사용 가능하도록 허용

        /*
        // Spring security는 기본적으로 모든 페이지를 막기 때문에 누구나 접근할 수 있는 페이지가 필요할 경우(/)는 아래와 같이 설정이 필요하다.
        http.authorizeHttpRequests((requests) ->
                requests.antMatchers("/").permitAll()
                        .anyRequest().authenticated());

        http.httpBasic();*/
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }






}
