package stg.onyou.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    private String nickName;
    private LocalDateTime birthdate;
    private char sex;
    private String accountEmail;
    private LocalDateTime created;
    private LocalDateTime updated;

    @OneToMany(mappedBy = "creator")
    private List<Club> clubs = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<UserClub> userClubs = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Feed> feeds = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();




}
