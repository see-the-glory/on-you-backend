package stg.onyou.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.Role;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
//    private Integer organizationId;
    private String nickName;
    private LocalDateTime birthdate;
    private char sex;
    private String accountEmail;
    private LocalDateTime created;
    private LocalDateTime updated;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String socialId;

    //@OneToMany(mappedBy = "creator")
    //private List<Club> clubs;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<UserClub> userClubs;
    @ManyToOne(fetch = FetchType.EAGER)
    private Organization organization;


}
