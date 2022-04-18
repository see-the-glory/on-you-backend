package stg.onyou.model.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Integer id;
    private String hashTag;

    @OneToMany(mappedBy = "hashtag")
    private List<FeedHashtag> feedHashtags = new ArrayList<>();


}
