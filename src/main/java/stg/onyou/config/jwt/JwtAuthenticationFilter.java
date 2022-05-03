package stg.onyou.config.jwt;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import stg.onyou.config.auth.PrincipalDetails;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter{

    private static final String LOGIN_PREFIX = "/login/kakao/";
    private final String ACCESS_TOKEN_HEADER_NAME = "Authorization";



    /* /login/kakao/* 로 요청이 왔을 때 JwtAuthenticationFilter에서 해당 처리를 수행하기 위한 설정 */
    private static final AntPathRequestMatcher LOGIN_MATCHER=
            new AntPathRequestMatcher(LOGIN_PREFIX +"*", "GET"); //=>   /login/kakao/ 의 요청에, GET으로 온 요청에 매칭된다.

    public JwtAuthenticationFilter(AccessTokenAuthenticationProvider accessTokenAuthenticationProvider,   //Provider를 등록
                                   AuthenticationSuccessHandler authenticationSuccessHandler,  //로그인 성공 시 처리할  handler이다
                                   AuthenticationFailureHandler authenticationFailureHandler) { //로그인 실패 시 처리할 handler이다.

        super(LOGIN_MATCHER);   // 위에서 설정한  /login/kakao/* 의 요청에, GET으로 온 요청을 처리하기 위해 설정한다.

        this.setAuthenticationManager(new ProviderManager(accessTokenAuthenticationProvider));
        //AbstractAuthenticationProcessingFilter를 커스터마이징 하려면  ProviderManager를 꼭 지정해 주어야 한다(안그러면 예외남!!!)

        this.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        this.setAuthenticationFailureHandler(authenticationFailureHandler);

    }

    // Authentication 객체 만들어서 리턴 => 의존 : AuthenticationManager
    // 인증 요청시에 실행되는 함수 => /login
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        System.out.println("JwtAuthenticationFilter : 진입");
        String accessToken = request.getHeader(ACCESS_TOKEN_HEADER_NAME); //헤더의 AccessToken에 해당하는 값을 가져온다.

        Authentication authentication =
                this.getAuthenticationManager().authenticate(new AccessTokenKakao(accessToken));

        PrincipalDetails principalDetailis = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("Authentication : "+principalDetailis.getUser().getName());
        return authentication;
    }

    // JWT Token 생성해서 response에 담아주기
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        PrincipalDetails principalDetailis = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject(principalDetailis.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIRATION_TIME))
                .withClaim("id", principalDetailis.getUser().getId())
                .withClaim("username", principalDetailis.getUser().getName())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+jwtToken);
    }

}