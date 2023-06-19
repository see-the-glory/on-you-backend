package stg.onyou.model.entity;

import lombok.*;
import stg.onyou.model.enums.AccountLogType;
import stg.onyou.model.enums.ReportReason;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;

    @Enumerated(EnumType.STRING)
    private AccountLogType logType;
    private LocalDateTime created;
}
