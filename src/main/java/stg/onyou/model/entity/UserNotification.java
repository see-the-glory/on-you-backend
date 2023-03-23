package stg.onyou.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="recipient_id")
    private User recipient;

    @ManyToOne
    @JoinColumn(name="action_id")
    private Action action;

    private LocalDateTime created;
    private LocalDateTime readAt;
}
