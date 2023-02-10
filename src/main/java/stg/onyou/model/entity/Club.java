package stg.onyou.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import stg.onyou.model.RecruitStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@ToString(of={"id", "name", "shortDesc", "longDesc", "delYn", "thumbnail", "announcement", "recruitStatus", "maxNumber", "isApproveRequired"})
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String shortDesc;
    private String longDesc;
    private char delYn;
    private String thumbnail;
    @Enumerated(EnumType.STRING)
    private RecruitStatus recruitStatus;
    private int maxNumber;
    private int recruitNumber;
    private int clubLikesNumber;
    private int feedNumber;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String isApproveRequired;
    private String contactPhone;

    @OneToMany(mappedBy = "club")
    @JsonIgnore
    @JsonManagedReference
    private List<UserClub> userClubs;


    @OneToMany(mappedBy = "club", cascade = CascadeType.REMOVE)
    private List<ClubCategory> clubCategories;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User creator;

    @OneToMany(mappedBy = "club")
    private List<Feed> feeds = new ArrayList<>();

}
