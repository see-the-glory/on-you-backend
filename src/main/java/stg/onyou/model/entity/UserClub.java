package stg.onyou.model.entity;

import lombok.*;
import stg.onyou.model.ApplyStatus;
import stg.onyou.model.Role;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@ToString(of={"id","applyStatus","approveDate", "applyDate", "role"})
public class UserClub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="club_id")
    private Club club;

    @Enumerated(EnumType.STRING)
    private ApplyStatus applyStatus;
    private LocalDateTime approveDate;
    private LocalDateTime applyDate;
    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime updated;

}
