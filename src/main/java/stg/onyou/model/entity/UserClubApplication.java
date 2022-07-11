package stg.onyou.model.entity;

import lombok.*;
import stg.onyou.model.Action;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserClubApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="club_id")
    private Club club;

    @ManyToOne
    @JoinColumn(name="actioner_id")
    private User actioner;

    @ManyToOne
    @JoinColumn(name="actionee_id")
    private User actionee;

    @Enumerated(EnumType.STRING)
    private Action action;
    private String action_content;
}
