package com.example.diploma.model;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "post_comments")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private PostComment parentPostComment;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = Post.class)
    @JoinColumn(name = "post_id", referencedColumnName = "pid")
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private User user;
    @Column(columnDefinition = "datetime not null")
    private Instant time;
    @Column(columnDefinition = "text not null")
    private String text;
}
