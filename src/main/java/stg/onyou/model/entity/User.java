package stg.onyou.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import stg.onyou.model.network.request.UserCreateRequest;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@ToString(of={"id","name","birthday", "thumbnail", "sex", "email", "role"})
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
    private char sex;
    @Column(unique=true)
    private String email;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String phoneNumber;
    private String password;
    private String targetToken;
    private char userPushAlarm;
    private char clubPushAlarm;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> role = new ArrayList<>();

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
        return this.role.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
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
