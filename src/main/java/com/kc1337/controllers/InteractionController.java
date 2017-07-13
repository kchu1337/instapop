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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user =  userRepository.findByEmail(email);
        model.addAttribute("imageList", imageRepository.findAllByUser(user));
        return "userimages";
    }

    @RequestMapping("/userimages/{id}")
    public String viewUsers(@PathVariable("id") int id,Model model) {
        User user =  userRepository.findOne(id);
        model.addAttribute("imageList", imageRepository.findAllByUser(user));
        return "userimages";
    }

    @RequestMapping("/myfeed")
    public String viewFeed(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user =  userRepository.findByEmail(email);
        Set<User> followed = user.getFollowsList();
        ArrayList<Image> feedImages = new ArrayList<Image>();
        for(User u: followed){

        }

        model.addAttribute("imageList", imageRepository.findAllByUser(user));
        return "allimages";
    }

    @RequestMapping("/view/{id}")
    public String viewSingle(@PathVariable("id") long id, Model model) {
    Image image = imageRepository.findOne(id);
        model.addAttribute("comment", new Comment());
        String likeButton = isLiked(image)? "Click to Unlike": "Click to Like";
        String followButton = isFollower(image.getUser())? "Click to Unfollow": "Click to Follow";
        model.addAttribute("image", image);
        model.addAttribute("likeButton", likeButton);
        model.addAttribute("likeCount", image.likesCount());
        model.addAttribute("followButton", followButton);
        model.addAttribute("commentList", image.getComments());
        return "view";
    }

    @RequestMapping("/like/{id}")
    public String like (@PathVariable("id") long id) {
        Image image = imageRepository.findOne(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user =  userRepository.findByEmail(email);
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
    public String follow (@PathVariable("id") long id) {
        Image image = imageRepository.findOne(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User follower =  userRepository.findByEmail(email);
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user =  userRepository.findByEmail(email);
        comment.setImage(image);
        comment.setUser(user);
        commentRepository.save(comment);

        return "redirect:/view/"+id;
    }



    private boolean isLiked(Image image){
        boolean likes = false;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user =  userRepository.findByEmail(email);
        for(User u: image.getLikes()){
            likes = likes || (u.getId()==user.getId());
        }
        return likes;
    }

    private boolean isFollower(User uploader){
        boolean follow = false;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user =  userRepository.findByEmail(email);
        for(User u: user.getFollowsList()){
            follow = follow || (u.getId()==uploader.getId());
        }
        return follow;
    }
}
