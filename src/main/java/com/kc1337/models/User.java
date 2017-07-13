package com.kc1337.models;

import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by student on 6/28/17.
 */
@Entity
@Table(name = "userdata")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String password;
    private String name;
    private String email;
    @Value("USER")
    private String authority;
    @Value("TRUE")
    private boolean enabled;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private Set<Image> images;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private Set<Comment> comments;
    @ManyToMany
    @JoinTable(name="likes",
            joinColumns=
            @JoinColumn(name="user_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="image_id", referencedColumnName="id")
    )
    private Set<Image> likes;

    @ManyToMany
    @JoinTable(name="follow",
            joinColumns=
            @JoinColumn(name="follower_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="followed_id", referencedColumnName="id")
    )
    private Set<User> followsList;

    @ManyToMany
    @JoinTable(name="follow",
            joinColumns=
            @JoinColumn(name="followed_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="follower_id", referencedColumnName="id")
    )
    private Set<User> followerList;

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

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    public Set<User> getFollowsList() {
        return followsList;
    }

    public void setFollowsList(Set<User> followsList) {
        this.followsList = followsList;
    }

    public Set<User> getFollowerList() {
        return followerList;
    }

    public void setFollowerList(Set<User> followerList) {
        this.followerList = followerList;
    }

    public Set<Image> getLikes() {
        return likes;
    }

    public void setLikes(Set<Image> likes) {
        this.likes = likes;
    }

    public boolean equals(User user2){
        return user2.getId() == this.id;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
}
