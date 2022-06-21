package stg.onyou.model.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String hashtag;

    @OneToMany(mappedBy = "hashtag")
    private List<FeedHashtag> feedHashtags = new ArrayList<>();


}
