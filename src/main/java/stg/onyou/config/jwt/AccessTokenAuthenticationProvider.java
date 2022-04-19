package stg.onyou.config.jwt;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import stg.onyou.config.auth.LoadUserService;
import stg.onyou.config.auth.PrincipalDetails;
import stg.onyou.model.Role;
import stg.onyou.model.entity.User;
import stg.onyou.repository.UserRepository;

@RequiredArgsConstructor
@Component
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {//AuthenticationProvider을 구현받아 authenticate와 supports를 구현해야 한다.

    private final LoadUserService loadUserService;  //restTemplate를 통해서 AccessToken을 가지고 회원의 정보를 가져오는 역할을 한다.
    private final UserRepository userRepository;//받아온 정보를 통해 DB에서 회원을 조회하는 역할을 한다.

    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {//ProviderManager가 호출한다. 인증을 처리한다

        PrincipalDetails oAuth2User = loadUserService.getOAuth2UserDetails((AccessTokenKakao) authentication);
        //OAuth2UserDetails는  UserDetails를 상속받아 구현한 클래스이다. 이후 일반 회원가입 시 UserDetails를 사용하는 부분과의 다형성을 위해 이렇게 구현하였다.
        //getOAuth2UserDetails에서는 restTemplate과 AccessToken을 가지고 회원정보를 조회해온다 (식별자 값을 가져옴)


        User user = saveOrGet(oAuth2User);//받아온 식별자 값과 social로그인 방식을 통해 회원을 DB에서 조회 후 없다면 새로 등록해주고, 있다면 그대로 반환한다.
         // oAuth2User.setRoles(member.getRole().name());//ADMIN, USER, GUEST

        return AccessTokenKakao.builder().principal(oAuth2User).authorities(oAuth2User.getAuthorities()).build();
        //AccessTokenSocialTypeToken객체를 반환한다. principal은 OAuth2UserDetails객체이다. (formLogin에서는 UserDetails를 가져와서 결국 ContextHolder에 저장하기 때문에)
        //이렇게 구현하면 UserDetails 타입으로 회원의 정보를 어디서든 조회할 수 있다.
    }


    private User saveOrGet(PrincipalDetails oAuth2User) {
        return userRepository.findBySocialId(oAuth2User.getSocialId())  //socailID(식별값)과 어떤 소셜 로그인 유형인지를 통해 DB에서 조회
                .orElseGet(() -> userRepository.save(User.builder()
                        .socialId(oAuth2User.getSocialId())
                        .role(Role.USER).build()));//없다면 멤버를 새로 만드는데, USER가 아니라 GUEST로 설정했다. 이는 아래해서 설명한다
    }



    @Override
    public boolean supports(Class<?> authentication) {
        return AccessTokenKakao.class.isAssignableFrom(authentication); //AccessTokenSocialTypeToken타입의  authentication 객체이면 해당 Provider가 처리한다.
    }

}
