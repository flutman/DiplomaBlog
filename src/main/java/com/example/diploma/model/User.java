package com.example.diploma.model;

import com.example.diploma.model.enums.UserRole;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "is_moderator", columnDefinition = "tinyint not null")
    private int isModerator;
    @Column(name = "reg_time", columnDefinition = "datetime not null")
    private LocalDateTime regTime;
    @Column(columnDefinition = "varchar(255) not null")
    private String name;
    @Column(columnDefinition = "varchar(255) not null")
    private String email;
    @Column(columnDefinition = "varchar(255) not null")
    private String password;
    @Column(columnDefinition = "varchar(255)")
    private String code;
    @Column(columnDefinition = "text")
    private String photo;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<PostVote> votes = new ArrayList<>();

    public UserRole getRole(){
        return isModerator == 1 ? UserRole.MODERATOR : UserRole.USER;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsModerator() {
        return isModerator;
    }

    public void setIsModerator(int isModerator) {
        this.isModerator = isModerator;
    }

    public LocalDateTime getRegTime() {
        return regTime;
    }

    public void setRegTime(LocalDateTime regTime) {
        this.regTime = regTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
