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
public class ClubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="club_id")
    private Club club;

    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;

    private LocalDateTime created;
    private LocalDateTime updated;

    private int order;

}
