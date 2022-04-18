package stg.onyou.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class FeedDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private User user;
    @ManyToOne
    private Club club;
    private String title;
    private String content;
    private String access;
    private char delYn;
    private LocalDateTime created;
    private LocalDateTime updated;
    @Column(columnDefinition = "integer default 0")
    private Integer reportCount;
}