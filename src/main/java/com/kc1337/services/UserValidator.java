package com.kc1337.services;

import com.kc1337.models.*;
import com.kc1337.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Autowired
    UserRepository userRepository;
    public boolean supports(Class<?> clazz){
        return User.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors){
        User user = (User) target;
        String email = user.getEmail();
        String password = user.getPassword();
        String authority = user.getAuthority();
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "user.name.empty");;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "user.email.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "user.password.empty");
        if(userRepository.countByEmail(email)>0){
            errors.rejectValue("email","user.email.duplicate");
        }
        if(password.length() < 5){
            errors.rejectValue("password","user.password.tooShort");
        }
        if(authority == null || authority.length()<1){
            errors.rejectValue("authority","user.authority.notChoose");
        }
    }
}