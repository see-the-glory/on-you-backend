package stg.onyou.config.jwt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import stg.onyou.config.auth.PrincipalDetails;
import stg.onyou.model.entity.User;
import stg.onyou.repository.UserRepository;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter{

    private static final String LOGIN_PREFIX = "/login/kakao/";
    private ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    private final UserRepository userRepository;


    /* /login/kakao/* 로 요청이 왔을 때 JwtAuthenticationFilter에서 해당 처리를 수행하기 위한 설정 */
    private static final AntPathRequestMatcher LOGIN_MATCHER=
            new AntPathRequestMatcher(LOGIN_PREFIX +"*", "POST"); //=>   /login/kakao/ 의 요청에, GET으로 온 요청에 매칭된다.

    public JwtAuthenticationFilter(AccessTokenAuthenticationProvider accessTokenAuthenticationProvider,   //Provider를 등록
                                   AuthenticationSuccessHandler authenticationSuccessHandler,  //로그인 성공 시 처리할  handler이다
                                   AuthenticationFailureHandler authenticationFailureHandler, UserRepository userRepository) { //로그인 실패 시 처리할 handler이다.

        super(LOGIN_MATCHER);   // 위에서 설정한  /login/kakao/* 의 요청에, GET으로 온 요청을 처리하기 위해 설정한다.
        this.userRepository = userRepository;

        this.setAuthenticationManager(new ProviderManager(accessTokenAuthenticationProvider));
        //AbstractAuthenticationProcessingFilter를 커스터마이징 하려면  ProviderManager를 꼭 지정해 주어야 한다(안그러면 예외남!!!)

        this.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        this.setAuthenticationFailureHandler(authenticationFailureHandler);

    }

    // Authentication 객체 만들어서 리턴 => 의존 : AuthenticationManager
    // 인증 요청시에 실행되는 함수 => /login
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException ,ServletException, IOException{

        System.out.println("JwtAuthenticationFilter : 진입");

        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        Token accessToken = objectMapper.readValue(messageBody, Token.class);

        return this.getAuthenticationManager().authenticate(new AccessTokenKakao(accessToken.getToken()));
            //AuthenticationManager에게 인증 요청을 보낸다. 이때 Authentication 객체로는 AccessTokenSocialTypeToken을(직접 커스텀 함) 사용한다.
    }

    // JWT Token 생성해서 response에 담아주기
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {


        PrincipalDetails principalDetailis = (PrincipalDetails) authResult.getPrincipal();

        User user = userRepository.findBySocialId(principalDetailis.getSocialId()).get();

        String jwtToken = JWT.create()
                .withSubject(principalDetailis.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIRATION_TIME))
                .withClaim("id", user.getId())
                .withClaim("socialId", user.getSocialId())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));


        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        // token 객체
        Token token = new Token();
        token.setToken(jwtToken);
        //json 형태로 바꾸기
        String result = objectMapper.writeValueAsString(token);
        response.getWriter().write(result);
    }

}
