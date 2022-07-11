package stg.onyou.config.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import stg.onyou.config.auth.LoadUserService;
import stg.onyou.config.auth.PrincipalDetails;
import stg.onyou.model.Role;
import stg.onyou.model.entity.*;
import stg.onyou.repository.UserRepository;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class AccessTokenAuthenticationProvider implements AuthenticationProvider { //AuthenticationProvider을 implement하여 authenticate와 supports를 구현

    private final LoadUserService loadUserService;  //restTemplate를 통해서 AccessToken을 가지고 회원의 정보를 가져오는 역할
    private final UserRepository userRepository;    //받아온 정보를 통해 DB에서 회원을 조회하는 역할

    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException { //ProviderManager가 호출함

        //OAuth2UserDetails는 UserDetails를 상속받아 구현한 클래스이다. 이후 일반 회원가입 시 UserDetails를 사용하는 부분과의 다형성을 위해 이렇게 구현하였다.
        //getOAuth2UserDetails에서는 restTemplate과 AccessToken을 가지고 회원정보를 조회해온다 (식별자 값을 가져옴)
        PrincipalDetails kakaoUser = loadUserService.getOAuth2UserDetails((AccessTokenKakao) authentication);

        User user = saveOrGet(kakaoUser);// 받아온 socialId 값으로 회원을 DB에서 조회 후 없다면 새로 등록해주고, 있다면 그대로 반환한다.
         // oAuth2User.setRoles(member.getRole().name());// MEMBER, MANAGER, MASTER

        return AccessTokenKakao.builder().principal(kakaoUser).authorities(kakaoUser.getAuthorities()).build();
        //AccessTokenKakao 객체를 반환한다. principal은 KakaoUserDetails객체이다.
        //이렇게 구현하면 UserDetails 타입으로 회원의 정보를 어디서든 조회할 수 있다.
    }


    private User saveOrGet(PrincipalDetails kakaoUser) {
        return userRepository.findBySocialId(kakaoUser.getSocialId())
                .orElseGet(() -> userRepository.save(User.builder()
                        .name(kakaoUser.getName())
                        .account_email(kakaoUser.getEmail())
                        .socialId(kakaoUser.getSocialId())
                        .birthday(kakaoUser.getBirthday())
                        .thumbnail(kakaoUser.getThumbnail())
                        .sex(kakaoUser.getSex())
                        .created(LocalDateTime.now())
                        .build()));

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AccessTokenKakao.class.isAssignableFrom(authentication); //AccessTokenKakao타입의 authentication 객체이면 해당 Provider가 처리한다.
    }

}
