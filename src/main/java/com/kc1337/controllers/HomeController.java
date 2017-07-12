package com.kc1337.controllers;

import com.cloudinary.utils.ObjectUtils;
import com.kc1337.configs.*;
import com.kc1337.models.*;
import com.kc1337.repositories.*;
import com.kc1337.services.*;

import com.google.common.collect.Lists;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.internet.InternetAddress;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by student on 7/10/17.
 */
@Controller
public class HomeController {



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

        return "home";
    }

    @RequestMapping("/login")
    public String login(Model model){

        return "login";
    }

    @GetMapping("/upload")
    public String uploadForm(Model model){
        model.addAttribute("image", new Image());
        return "upload";
    }
    @PostMapping("/upload")
    public String singleImageUpload(@RequestParam("file") MultipartFile file,  RedirectAttributes redirectAttributes,@ModelAttribute Image image, Model model){
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
            model.addAttribute("sizedimageurl", cloudc.createUrl(filename,300,400, "scale"));
            image.setImgname(filename);
            image.setImgsrc((String)  cloudc.createUrl(filename,300,400, "scale"));
            imageRepository.save(image);
            model.addAttribute("imageList", imageRepository.findAll());
        } catch (IOException e){
            e.printStackTrace();
            model.addAttribute("message", "Sorry I can't upload that!");
        }
        return "upload";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";

    }

    @RequestMapping(value="/register", method = RequestMethod.GET)
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }

}
