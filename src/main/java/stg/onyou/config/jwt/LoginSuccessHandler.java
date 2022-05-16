package stg.onyou.config.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import stg.onyou.model.Role;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("로그인 성공!!!! :"+authentication.getPrincipal());



        if(authentication.getAuthorities().stream().anyMatch(s -> s.getAuthority().equals(Role.MEMBER))){
            System.out.println("회원가입으로 이동합니다");
            response.sendRedirect("/signUp");
            return;
        }

        System.out.println("회원가입 한 사용자입니다.");

        //if(authentication.getAuthorities().stream().forEach(grantedAuthority -> grantedAuthority.getAuthority()))
    }
}

