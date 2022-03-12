package stg.onyou.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="organization")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String address;
    private String pastorName;
    private LocalDateTime created;

    @OneToMany(mappedBy = "organization")
    private List<User> users;
    @OneToMany(mappedBy = "organization")
    private List<Club> clubs;
}
