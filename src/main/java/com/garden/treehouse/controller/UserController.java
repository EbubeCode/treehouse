package com.garden.treehouse.controller;

import com.garden.treehouse.events.ForgotPasswordEvent;
import com.garden.treehouse.model.User;
import com.garden.treehouse.services.TokenService;
import com.garden.treehouse.services.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Objects;

@Controller
public class UserController {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserService userService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userSecurityService;


    public UserController(ApplicationEventPublisher applicationEventPublisher, UserService userService,
                          TokenService tokenService, PasswordEncoder passwordEncoder, UserDetailsService userSecurityService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.userService = userService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.userSecurityService = userSecurityService;
    }


    @GetMapping("/login")
    public String login(Principal principal) {
        if (principal == null)
            return "login";
        return "index";
    }

    @RequestMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/forgotPassword")
    public String forgetPassword(Principal principal, Model model) {
        if (principal != null) {
            return "forward:/";
        }
        model.addAttribute("errors", null);
        model.addAttribute("email", null);
        return "forget_password";
    }

    @PostMapping("/forgotPassword")
    public String forgetPassword(Model model, @ModelAttribute String email) {
        var user = userService.findByEmail(email);
        if (email != null) {
            applicationEventPublisher.publishEvent(new ForgotPasswordEvent(email));
            return "email_verification_consent";
        }
        model.addAttribute("errors", "No account is linked to this email");
        return "forget_password";
    }

    @PostMapping("/signup")
    public String newUserPost(
            HttpServletRequest request,
            @ModelAttribute("user") User user, Model model) throws Exception {
        if (!user.getPassword().equals(user.getMatchingPassword())) {
            model.addAttribute("errors", "Passwords don't match");
            return "signup";
        }

        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("errors", "Email already exists");

            return "signup";
        }


        userService.createUser(user);

        return "registration_consent";
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token_id") String tokenId,
                              @RequestParam("is_password") boolean password,
                              Model model, Principal principal) {
        if (principal != null) {
            return "forward:/";
        }
        var response = tokenService.verifyToken(tokenId);

        return switch (response) {
            case INVALID_TOKEN -> {
                model.addAttribute("error", "to be invalid");
                yield "verification_error";
            }
            case EXPIRED_TOKEN -> {
                model.addAttribute("error", "to have expired");
                yield "verification_error";
            }
            case VALID_TOKEN -> {
                if (password) {
                    var email = response.userEmail;
                    yield "forward:/updatePassword?email=" + email;
                }
                yield "forward:/myAccount";
            }

        };
    }

    //    @RequestMapping("/newUser")
//    public String newUser(Locale locale, @RequestParam("token") String token, Model model) {
//        PasswordResetToken passToken = userService.getPasswordResetToken(token);
//
//        if (passToken == null) {
//            String message = "Invalid Token.";
//            model.addAttribute("message", message);
//            return "redirect:/badRequest";
//        }
//
//        User user = passToken.getUser();
//        String username = user.getEmail();
//
//        UserDetails userDetails = userSecurityService.loadUserByUsername(username);
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
//                userDetails.getAuthorities());
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        model.addAttribute("user", user);
//
//        model.addAttribute("classActiveEdit", true);
//        model.addAttribute("orderList", user.getOrderList());
//        return "myProfile";
//    }
    @GetMapping("/signup")
    public String signUp(Model model, Principal principal) {
        if (principal != null) {
            return "forward:/";
        }
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("errors", null);

        return "signup";
    }

    @GetMapping("/updatePassword")
    public String updatePassword(Model model, @RequestParam("email") String email) {
        model.addAttribute("errors", null);
        model.addAttribute("password", "");
        model.addAttribute("matchingPassword", "");
        model.addAttribute("email", email);
        return "new_password";
    }

    @PostMapping("/updatePassword")
    public String update(@RequestParam("email") String email, @ModelAttribute("password") String password,
                         @ModelAttribute("matchingPassword") String matchingPassword, Model model) {
        if (!password.equals(matchingPassword)) {
        model.addAttribute("errors", "passwords do not match");
        return "new_password";
        }
        var user = userService.findByEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userService.save(user);

        return "forward:/myAccount";
    }

    @GetMapping("/myAccount")
    public String myAccount(Model model, Principal principal) {


        return "myAccount";
    }

    @GetMapping("/myProfile")
    public String myProfile(Model model, Principal principal) {
        var user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "myProfile";
    }

    @GetMapping("/updateUserInfo")
    public String updateUserInfo(Model model, Principal principal) {
        var user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("errors", null);
        return "updateUser";
    }


    @PostMapping("/updateUserInfo")
    public String updateUserInfo(
            Principal principal,
            @ModelAttribute("user") User user,
            Model model
    ) throws Exception {
        User currentUser = userService.findByEmail(principal.getName());

        if (currentUser == null) {
            throw new Exception("User not found");
        }

        /*check email already exists*/
        if (userService.findByEmail(user.getEmail()) != null) {
            if (!userService.findByEmail(user.getEmail()).getId().equals(currentUser.getId())) {
                model.addAttribute("errors", "Email already linked to another account");
                return "updateUser";
            }
        }



        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());

        userService.save(currentUser);


        UserDetails userDetails = userSecurityService.loadUserByUsername(currentUser.getEmail());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/myProfile";
    }

}
