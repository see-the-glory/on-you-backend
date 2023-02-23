package stg.onyou.model.entity;

import lombok.*;
import org.hibernate.annotations.Type;
import stg.onyou.model.ActionType;
import stg.onyou.model.ApplyStatus;
import stg.onyou.model.Role;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="action_club_id")
    private Club actionClub;

    @ManyToOne
    @JoinColumn(name="action_feed_id")
    private Feed actionFeed;

    @ManyToOne
    @JoinColumn(name="actioner_id")
    private User actioner;
    @ManyToOne
    @JoinColumn(name="actionee_id")
    private User actionee;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;
    private String applyMessage;

    @Enumerated(EnumType.STRING)
    private Role changedRole;
    @Column(columnDefinition = "TINYINT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isProcessDone;
    private LocalDateTime created;

    @OneToMany(mappedBy = "action",  cascade = CascadeType.REMOVE)
    private List<UserNotification> userNotifications = new ArrayList<>();
    @OneToMany(mappedBy = "action",  cascade = CascadeType.REMOVE)
    private List<ClubNotification> clubNotifications = new ArrayList<>();

}
