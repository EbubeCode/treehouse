package com.garden.treehouse.controller;

import com.garden.treehouse.events.ForgotPasswordEvent;
import com.garden.treehouse.model.User;
import com.garden.treehouse.model.UserBilling;
import com.garden.treehouse.model.UserPayment;
import com.garden.treehouse.model.UserShipping;
import com.garden.treehouse.services.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class UserController {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserService userService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userSecurityService;
    private final OrderService orderService;
    private final UserShippingService userShippingService;
    private final UserPaymentService userPaymentService;


    public UserController(ApplicationEventPublisher applicationEventPublisher, UserService userService, TokenService tokenService, PasswordEncoder passwordEncoder, UserDetailsService userSecurityService, OrderService orderService, UserShippingService userShippingService, UserPaymentService userPaymentService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.userService = userService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.userSecurityService = userSecurityService;
        this.orderService = orderService;
        this.userShippingService = userShippingService;
        this.userPaymentService = userPaymentService;
    }


    @GetMapping("/login")
    public String login(Principal principal) {
        if (principal == null) return "login";
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
        if (user != null) {
            applicationEventPublisher.publishEvent(new ForgotPasswordEvent(email));
            return "email_verification_consent";
        }
        model.addAttribute("errors", "No account is linked to this email");
        return "forget_password";
    }

    @PostMapping("/signup")
    public String newUserPost(@ModelAttribute("user") User user, Model model) {
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
    public String verifyEmail(@RequestParam("token_id") String tokenId, @RequestParam("is_password") boolean password, Model model, Principal principal) {
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
    public String update(@RequestParam("email") String email, @ModelAttribute("password") String password, @ModelAttribute("matchingPassword") String matchingPassword, Model model) {
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
    public String myAccount() {

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
    public String updateUserInfo(Principal principal, @ModelAttribute("user") User user, Model model) throws Exception {
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

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/myProfile";
    }

    @GetMapping("/myOrders")
    public String myOrders(Model model, Principal principal) {
        var user = userService.findByEmail(principal.getName());

        model.addAttribute("orders", user.getOrderList());

        return "myOrders";
    }

    @GetMapping("/orderInfo")
    public String orderInfo(@RequestParam("id") Long id, Model model) {
        var order = orderService.findById(id);
        model.addAttribute("order", order);

        return "orderInfo";
    }

    @GetMapping("/myAddress")
    public String myAddress(Model model, Principal principal) {
        var user = userService.findByEmail(principal.getName());
        var shippingList = user.getUserShippingList();
        model.addAttribute("shippingList", shippingList);

        return "myShipping";
    }

    @GetMapping("/addShipping")
    public String updateShipping(@RequestParam(name = "id", required = false) Long id, Model model) {
        if (id == null) {
            var userShipping = new UserShipping();
            model.addAttribute("userShipping", userShipping);
            model.addAttribute("title", "Add Address");
            model.addAttribute("id", 0);
            return "addAddress";

        }

        var userShipping = userShippingService.findById(id);
        model.addAttribute("userShipping", userShipping);
        model.addAttribute("title", "Update Address");
        model.addAttribute("id", id);
        return "addAddress";
    }

    @PostMapping("/addShipping")
    public String add_updateShipping(@RequestParam(name = "id") Long id,
                                     @ModelAttribute UserShipping userShipping,
                                     Principal principal) {
        var user = userService.findByEmail(principal.getName());
        if (id != 0) {
        var opShipping = userShippingService.findById(id);

            convertShipping(opShipping, userShipping);
            userService.updateUserShipping(opShipping, user, opShipping.isUserShippingDefault());
        }
        else
            userService.updateUserShipping(userShipping, user, userShipping.isUserShippingDefault());

        return "redirect:/myAddress";
    }

    private void convertShipping(UserShipping old, UserShipping newShipping) {
        old.setUserShippingDefault(newShipping.isUserShippingDefault());
        old.setUserShippingStreet(newShipping.getUserShippingStreet());
        old.setUserShippingCity(newShipping.getUserShippingCity());
        old.setUserShippingState(newShipping.getUserShippingState());
        old.setUserShippingCountry(newShipping.getUserShippingCountry());
        old.setUserShippingZipcode(newShipping.getUserShippingZipcode());
    }

    @DeleteMapping("/removeUserShipping")
    @ResponseBody
    public Long removeUserShipping(@RequestParam("id") Long userShippingId) {

        userShippingService.deleteById(userShippingId);
        return userShippingId;
    }


    @GetMapping("/myPayment")
    public String myPayment(Model model, Principal principal) {
        var user = userService.findByEmail(principal.getName());
        var payments = user.getUserPaymentList();
        model.addAttribute("payments", payments);

        return "myPayment";
    }

    @GetMapping("/addPayment")
    public String updatePayment(@RequestParam(name = "id", required = false) Long id, Model model) {
        if (id == null) {
            var userPayment = new UserPayment();
            var userBilling = new UserBilling();
            model.addAttribute("userPayment", userPayment);
            model.addAttribute("userBilling", userBilling);
            model.addAttribute("title", "Add Payment");
            model.addAttribute("id", 0);
            return "addPayment";

        }

        var userPayment = userPaymentService.findById(id);
        model.addAttribute("userPayment", userPayment);
        model.addAttribute("userBilling", userPayment.getUserBilling());
        model.addAttribute("title", "Update Payment");
        model.addAttribute("id", id);
        return "addPayment";
    }

    @PostMapping("/addPayment")
    public String add_updatePayment(@RequestParam(name = "id") Long id,
                                     @ModelAttribute UserPayment userPayment,
                                     @ModelAttribute UserBilling userBilling,
                                     Principal principal) {
        var opPayment = userPaymentService.findById(id);
        var user = userService.findByEmail(principal.getName());
        if (opPayment != null) {
            var oldUserBilling = opPayment.getUserBilling();
            convertPayment(opPayment, userPayment, oldUserBilling, userBilling);
            userService.updateUserBilling(oldUserBilling, opPayment, user, opPayment.isDefaultPayment());
        }
        else
            userService.updateUserBilling(userBilling, userPayment, user, userPayment.isDefaultPayment());

        return "redirect:/myPayment";
    }

    private void convertPayment(UserPayment old, UserPayment newPayment, UserBilling oldBilling, UserBilling newBilling) {
        old.setDefaultPayment(newPayment.isDefaultPayment());
        old.setHolderName(newPayment.getHolderName());
        old.setCardName(newPayment.getCardName());
        old.setCardNumber(newPayment.getCardNumber());
        old.setCardType(newPayment.getCardType());
        old.setExpiryMonth(newPayment.getExpiryMonth());
        old.setExpiryYear(newPayment.getExpiryYear());
        old.setCvc(newPayment.getCvc());

        oldBilling.setUserBillingStreet(newBilling.getUserBillingStreet());
        oldBilling.setUserBillingCity(newBilling.getUserBillingCity());
        oldBilling.setUserBillingState(newBilling.getUserBillingState());
        oldBilling.setUserBillingCountry(newBilling.getUserBillingCountry());
        oldBilling.setUserBillingZipcode(newBilling.getUserBillingZipcode());
    }

    @DeleteMapping("/removeUserPayment")
    @ResponseBody
    public Long removeUserPayment(@RequestParam("id") Long paymentId) {

        userPaymentService.deleteById(paymentId);
        return paymentId;
    }

    @GetMapping(value = "/userShipping/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserShipping getUserShipping(@PathVariable Long id, Principal principal){
        var user = userService.findByEmail(principal.getName());

        return user.getUserShippingList().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @GetMapping(value = "/userPayment/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserPayment getUserPayment(@PathVariable Long id, Principal principal){
        var user = userService.findByEmail(principal.getName());

        return user.getUserPaymentList().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
