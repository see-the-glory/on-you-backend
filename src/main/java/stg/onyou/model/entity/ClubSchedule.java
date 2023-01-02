package stg.onyou.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(of={"id", "name", "location", "content", "startDate", "endDate"})
public class ClubSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Club club;
    private String name;
    private String location;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime created;
    private LocalDateTime updated;

}
