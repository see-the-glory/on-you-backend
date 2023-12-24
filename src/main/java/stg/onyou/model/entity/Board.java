package stg.onyou.model.entity;

import lombok.*;
import stg.onyou.model.enums.BoardCategory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String content;
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    private BoardCategory category;

    private int viewCount;
    private int reportCount;
    private int likeCount;
    private int commentCount;

    @NotNull
    private char delYn;
    @NotNull
    private LocalDateTime created;
    @NotNull
    private LocalDateTime updated;

}