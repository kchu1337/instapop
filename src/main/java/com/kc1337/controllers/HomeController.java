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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

/**
 * Created by student on 7/10/17.
 */
@Controller
public class HomeController {
//https://jsfiddle.net/jznbkcnp/1/


    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;

    @Autowired
    private UserValidator userValidator;
    @Autowired
    private PasswordEncoder passwordEncoder;



    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String home(Model model){

        return "index";
    }


    @GetMapping("/upload")
    public String uploadForm(Model model){
        model.addAttribute("image", new Image());
        return "upload";
    }
    @PostMapping("/upload")
    public String singleImageUpload(@RequestParam("file") MultipartFile file,  RedirectAttributes redirectAttributes,
                                    @ModelAttribute Image image, Model model){
        if (file.isEmpty()){
            model.addAttribute("message","Please select a file to upload");
            return "upload";
        }
        try {
            Map uploadResult =  cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            model.addAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");
            model.addAttribute("imageurl", uploadResult.get("url"));
            String filename = uploadResult.get("public_id").toString() + "." + uploadResult.get("format").toString();
            model.addAttribute("sizedimageurl", cloudc.createUrl(filename,300,400, "pad"));
            image.setImgname(filename);
            image.setImgsrc((String)  cloudc.createUrl(filename,300,400, "pad"));
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user =  userRepository.findByEmail(email);
            image.setUser(user);
            imageRepository.save(image);
            model.addAttribute("imageList", imageRepository.findAll());
        } catch (IOException e){
            e.printStackTrace();
            model.addAttribute("message", "Sorry I can't upload that!");
        }
        return "redirect:/filter/"+image.getId();
    }

    @RequestMapping("/filter/{id}")
    public String seeFilter(@PathVariable("id") long id, Model model){
        Image image = imageRepository.findOne(id);
        String filename = image.getImgname();
        model.addAttribute("imageId", id);
        model.addAttribute("original", cloudc.createUrl(filename,300,400, "pad"));
        model.addAttribute("bwUrl", cloudc.transformBWUrl(filename));
        model.addAttribute("sepiaUrl", cloudc.transformSepiaUrl(filename));
        model.addAttribute("pixelateUrl", cloudc.transformPixelateUrl(filename));
        return "filter";
    }

    @RequestMapping("/savefilter/{id}/{filter}")
    public String saveFilger(@PathVariable("id") long id, @PathVariable("filter") String filter, Model model){
        Image image = imageRepository.findOne(id);
        String filename = image.getImgname();
        switch (filter){
            case "original":
                image.setImgsrc(cloudc.createUrl(filename,300,400, "pad"));
                imageRepository.save(image);
                break;
            case "sepia":
                image.setImgsrc(cloudc.transformSepiaUrl(filename));
                imageRepository.save(image);
                break;
            case "bw":
                image.setImgsrc(cloudc.transformBWUrl(filename));
                imageRepository.save(image);
                break;
            case "pixelate":
                image.setImgsrc(cloudc.transformPixelateUrl(filename));
                imageRepository.save(image);
                break;
        }

        return "redirect:/";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";

    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String saveAccount(@Valid User user, BindingResult result, Model model){
        model.addAttribute("user", user);
        userValidator.validate(user,result);
        if (result.hasErrors()){
            return "createacc";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);
        return "redirect:/";
    }

}
