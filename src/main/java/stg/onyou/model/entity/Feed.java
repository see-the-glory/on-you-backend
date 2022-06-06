package stg.onyou.model.entity;

import lombok.*;
import stg.onyou.model.AccessModifier;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@ToString(of={"id", "content", "access", "delYn", "reportCount"})
public class Feed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    private String content;
    @Enumerated(EnumType.STRING)
    private AccessModifier access;
    private char delYn;
    private LocalDateTime created;
    private LocalDateTime updated;
    @Column(columnDefinition = "integer default 0")
    private Integer reportCount;

    @OneToMany(mappedBy = "feed")
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "feed")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "feed")
    private List<FeedHashtag> feedHashtags = new ArrayList<>();

    @OneToMany(mappedBy = "feed")
    private List<FeedImage> feedImages = new ArrayList<>();

}