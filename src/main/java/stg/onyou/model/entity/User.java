package stg.onyou.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import stg.onyou.model.network.request.UserCreateRequest;


import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@Entity
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;
    private String birthday;
    private String thumbnail;
    private String backgroundImage;
    private String sex;
    @Column(unique=true)
    private String email;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String phoneNumber;
    private String password;
    private String targetToken;
    private char userPushAlarm;
    private char clubPushAlarm;
    private LocalDateTime lastLoginDate;
    private String appVersion;
    private String deviceInfo;
    private String about;
    private String isEmailPublic;
    private String isContactPublic;
    private String isBirthdayPublic;
    private String isClubPublic;
    private String isFeedPublic;
    private String jwt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<UserClub> userClubs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<UserClubSchedule> userClubSchedule = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Feed> feeds = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<FeedLikes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<ClubLikes> clubLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Interest> interests = new ArrayList<>();

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.REMOVE)
    private List<UserNotification> userNotifications = new ArrayList<>();

    @OneToMany(mappedBy = "actioner", cascade = CascadeType.REMOVE)
    private List<Action> actioners = new ArrayList<>();

    @OneToMany(mappedBy = "actionee", cascade = CascadeType.REMOVE)
    private List<Action> actionees = new ArrayList<>();

    @OneToMany(mappedBy = "blocker", cascade = CascadeType.REMOVE)
    private List<UserBlock> blockers = new ArrayList<>();

    @OneToMany(mappedBy = "blockee", cascade = CascadeType.REMOVE)
    private List<UserBlock> blockees = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<GuestComment> guestComments = new ArrayList<>();

    public User(UserCreateRequest userCreateRequest) {
        birthday = userCreateRequest.getBirthday();
        sex = userCreateRequest.getSex();
        email = userCreateRequest.getEmail();
        name = userCreateRequest.getName();
        phoneNumber = userCreateRequest.getPhoneNumber();
        password = userCreateRequest.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
}
