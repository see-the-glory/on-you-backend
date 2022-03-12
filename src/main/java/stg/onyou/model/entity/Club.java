package stg.onyou.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name="club")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String information;
    private char delYn;
    private String thumbnail;
    private String announcement;
    private String recruitStatus;
    private int maxNumber;
    private LocalDateTime created;
    private LocalDateTime updated;

    @OneToMany(mappedBy = "club")
    @JsonIgnore
    private List<UserClub> userClubs;

    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;
    @ManyToOne(fetch = FetchType.EAGER)
    private Organization organization;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User creator;

}
