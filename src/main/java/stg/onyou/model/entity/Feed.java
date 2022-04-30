package stg.onyou.model.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Feed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    private String title;
    private String content;
    private String access;
    private char delYn;
    private LocalDateTime created;
    private LocalDateTime updated;
    @Column(columnDefinition = "integer default 0")
    private Integer reportCount;

    @OneToMany(mappedBy = "feed")
    private List<Good> likes = new ArrayList<>();

    @OneToMany(mappedBy = "feed")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "feed")
    private List<FeedHashtag> feedHashtags = new ArrayList<>();

    @OneToMany(mappedBy = "feed")
    private List<FeedImage> feedImages = new ArrayList<>();

}