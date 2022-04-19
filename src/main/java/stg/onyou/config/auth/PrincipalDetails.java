package stg.onyou.config.auth;

import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import stg.onyou.model.entity.User;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Collection;

@Builder
public class PrincipalDetails implements UserDetails {

    private User user;
    private String socialId;
    private String username;

    public PrincipalDetails(User user){
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       /* Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        user.getRoleList().forEach(r -> {
            authorities.add(()->{ return r;});
        });
        return authorities;*/
        return null;
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    @Override
    public String getPassword(){
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }

    public String getSocialId() {
        return socialId;
    }
}
