package stg.onyou.config.jwt;

import lombok.Builder;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AccessTokenKakao extends AbstractAuthenticationToken {

    private Object principal;//OAuth2UserDetails 타입
    private String accessToken;



    public AccessTokenKakao(String accessToken) {
        super(null);
        this.accessToken = accessToken;
        setAuthenticated(false);
    }


    @Builder
    public AccessTokenKakao(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true); // must use super, as we override
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
