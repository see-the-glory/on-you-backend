package stg.onyou.model.entity;
import lombok.*;
import stg.onyou.model.enums.PreferType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="prefer_club_id")
    private Club preferClub;

    @ManyToOne
    @JoinColumn(name="prefer_user_id")
    private User preferUser;

    @Enumerated(EnumType.STRING)
    private PreferType preferType;

    private LocalDateTime created;
}
