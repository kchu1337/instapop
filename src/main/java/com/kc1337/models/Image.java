package com.kc1337.models;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private String imgsrc;
    private String caption;
    private String imgname;
    @ManyToOne()
    @JoinColumn(name = "userdata_id")
    private User user;
    @OneToMany(mappedBy = "image", cascade = CascadeType.PERSIST)
    private Set<Comment> comments;
    @ManyToMany
    @JoinTable(name="likes",
            joinColumns=
            @JoinColumn(name="image_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="user_id", referencedColumnName="id")
    )
    private Set<User> likes;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImgname() {
        return imgname;
    }

    public void setImgname(String imgname) {
        this.imgname = imgname;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User userId) {
        this.user = userId;
    }

    public Set<User> getLikes() {
        return likes;
    }

    public void setLikes(Set<User> likes) {
        this.likes = likes;
    }

    public int likesCount() {
        return likes.size();
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public boolean equals(Image image2){
        return image2.getId() == this.id;
    }


}
