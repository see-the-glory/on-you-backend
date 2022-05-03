package stg.onyou.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import stg.onyou.config.jwt.JwtAuthenticationFilter;
import stg.onyou.config.jwt.JwtAuthorizationFilter;
import stg.onyou.repository.UserRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // prepost로 권한 체크를 하겠다는 의미이다.
public class SecurityConfig extends WebSecurityConfigurerAdapter { //이 클래스가 filter chain을 구성햐는 config 클래스이다.


    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CorsConfig corsConfig;
    /* configure 메소드에서 설정하는 것이 filter 프록시의 실제 filter들을 셋팅하는 역할을 함.
    url 패턴에 따라 다른 필터를 적용할 수 있으며, web resource의 경우는 ignore하는 것도 가능함 */
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .addFilter(corsConfig.corsFilter())
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
               // .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))
                .authorizeRequests()
                .antMatchers("/api/**")
                .access("hasRole('MEMBER') or hasRole('MANAGER') or hasRole('MASTER')")
                .antMatchers("/manager/**")
                .access("hasRole('MANAGER') or hasRole('MASTER')")
                .antMatchers("/admin/**")
                .access("hasRole('MASTER')")
                .anyRequest().permitAll();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }





}
