package main.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tag2post")
public class Tag2Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne
    private Post post;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Tag> tags;

}
