package stg.onyou.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.RecuritStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String short_desc;
    private String long_desc;
    private char delYn;
    private String thumbnail;
    private String announcement;
    @Enumerated(EnumType.STRING)
    private RecuritStatus recruitStatus;
    private int maxNumber;
    private LocalDateTime created;
    private LocalDateTime updated;

    @OneToMany(mappedBy = "club")
    @JsonIgnore
    @JsonManagedReference
    private List<UserClub> userClubs;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User creator;

}
