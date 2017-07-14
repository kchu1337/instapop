package com.kc1337.controllers;

        import com.cloudinary.utils.ObjectUtils;
        import com.kc1337.configs.*;
        import com.kc1337.models.*;
        import com.kc1337.repositories.*;
        import com.kc1337.services.*;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.security.core.Authentication;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.*;

        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.List;
        import java.util.Set;


/**
 * Created by student on 7/12/17.
 */
@Controller
public class InteractionController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    private UserValidator userValidator;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/allimages")
    public String viewAll(Model model) {

        model.addAttribute("imageList", imageRepository.findAll());
        return "allimages";
    }

    @RequestMapping("/myimages")
    public String viewMine(Model model) {
        User user =  getLoggedIn();
        model.addAttribute("imageList", imageRepository.findAllByUser(user));
        model.addAttribute("user", user);
        String followButton = isFollower(user)? "Click to Unfollow": "Click to Follow";
        model.addAttribute("followButton", followButton);
        return "userimages";
    }

    @RequestMapping("/userimages/{id}")
    public String viewUsers(@PathVariable("id") int id,Model model) {
        User user =  userRepository.findOne(id);
        model.addAttribute("imageList", imageRepository.findAllByUser(user));
        model.addAttribute("user", user);
        String followButton = isFollower(user)? "Click to Unfollow": "Click to Follow";
        model.addAttribute("followButton", followButton);
        return "userimages";
    }

    @RequestMapping("/myfeed")
    public String viewFeed(Model model) {
        User user =  getLoggedIn();
        Set<User> followed = user.getFollowsList();
        ArrayList<Image> feedImages = new ArrayList<Image>();
        for(User u: followed){
            ArrayList<Image> temp = (ArrayList<Image>) imageRepository.findAllByUser(u);
            feedImages.addAll(temp);
        }

        model.addAttribute("imageList", feedImages);
        return "allimages";
    }

    @RequestMapping("/view/{id}")
    public String viewSingle(@PathVariable("id") long id, Model model) {
    Image image = imageRepository.findOne(id);
        model.addAttribute("comment", new Comment());
        String likeButton = isLiked(image)? "Click to Unlike": "Click to Like";
        String followButton = isFollower(image.getUser())? "Click to Unfollow": "Click to Follow";
        Iterable<Comment> commentList =  commentRepository.findAllByImage(image);

        model.addAttribute("image", image);
        model.addAttribute("likeButton", likeButton);
        model.addAttribute("likeCount", image.likesCount());
        model.addAttribute("followButton", followButton);
        model.addAttribute("commentList", commentList);
        return "view";
    }

    @RequestMapping("/like/{id}")
    public String like (@PathVariable("id") long id) {
        Image image = imageRepository.findOne(id);
        User user =  getLoggedIn();
        if(isLiked(image)){
            image.getLikes().remove(user);
            imageRepository.save(image);
            user.getLikes().remove(image);
            userRepository.save(user);
        }
        else{
            image.getLikes().add(user);
            imageRepository.save(image);
            user.getLikes().add(image);
            userRepository.save(user);
        }

        return "redirect:/view/"+id;
    }

    @RequestMapping("/followfromimage/{id}")
    public String followImage (@PathVariable("id") long id) {
        Image image = imageRepository.findOne(id);
        User follower =  getLoggedIn();
        User uploader = image.getUser();
        if(isFollower(uploader)){
            uploader.getFollowerList().remove(follower);;
            follower.getFollowsList().remove(uploader);
            userRepository.save(follower);
        }
        else{
            follower.getFollowsList().add(uploader);
            userRepository.save(follower);
        }

        return "redirect:/view/"+id;
    }
    @PostMapping("/addcomment/{id}")
    public String addComment (@PathVariable("id") long id, @ModelAttribute Comment comment, Model model){
        model.addAttribute("comment", comment);
        Image image = imageRepository.findOne(id);
        User user =  getLoggedIn();
        comment.setImage(image);
        comment.setUser(user);
        commentRepository.save(comment);

        return "redirect:/view/"+id;
    }

    @RequestMapping("/followfromuser/{id}")
    public String followUser (@PathVariable("id") int id) {
        User uploader = userRepository.findOne(id);
        User follower =  getLoggedIn();
        if(isFollower(uploader)){
            uploader.getFollowerList().remove(follower);;
            follower.getFollowsList().remove(uploader);
            userRepository.save(follower);
        }
        else{
            follower.getFollowsList().add(uploader);
            userRepository.save(follower);
        }

        return "redirect:/userimages/"+id;
    }


    private boolean isLiked(Image image){
        boolean likes = false;
        User user =  getLoggedIn();
        for(User u: image.getLikes()){
            likes = likes || (u.getId()==user.getId());
        }
        return likes;
    }

    private boolean isFollower(User uploader){
        boolean follow = false;
        User user =  getLoggedIn();
        for(User u: user.getFollowsList()){
            follow = follow || (u.getId()==uploader.getId());
        }
        return follow;
    }

    private User getLoggedIn(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        return userRepository.findByName(name);

    }
}
