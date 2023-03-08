package stg.onyou.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import stg.onyou.config.jwt.JwtTokenInterceptor;
import stg.onyou.config.jwt.JwtTokenService;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final JwtTokenService jwtTokenService;

    public WebMvcConfig(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtTokenInterceptor(jwtTokenService))
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/user/login")
                .excludePathPatterns("/api/user/signup")
                .excludePathPatterns("/api/user/findId")
                .excludePathPatterns("/api/user/duplicateEmailCheck"
                )
                .excludePathPatterns("/api/mail/**")
                .excludePathPatterns("/api/test/**");

    }
}
