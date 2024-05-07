package stg.onyou.model.entity;

import lombok.*;
import stg.onyou.model.enums.BoardCategory;
import stg.onyou.model.network.response.BoardResponse;
import stg.onyou.service.LikesService;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private int commentCount;

    @NotNull
    private char delYn;
    @NotNull
    private LocalDateTime created;
    @NotNull
    private LocalDateTime updated;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<BoardLikes> likes = new ArrayList<>();

}