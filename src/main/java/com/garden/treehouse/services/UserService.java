package com.garden.treehouse.services;

import com.garden.treehouse.events.CreateUserEvent;
import com.garden.treehouse.model.*;
import com.garden.treehouse.model.security.Role;
import com.garden.treehouse.model.security.UserRole;
import com.garden.treehouse.repos.RoleRepository;
import com.garden.treehouse.repos.UserPaymentRepository;
import com.garden.treehouse.repos.UserRepository;
import com.garden.treehouse.repos.UserShippingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserPaymentRepository userPaymentRepository;
    private final UserShippingRepository userShippingRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher publisher;


    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       UserPaymentRepository userPaymentRepository, UserShippingRepository userShippingRepository,
                       PasswordEncoder passwordEncoder, ApplicationEventPublisher publisher) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userPaymentRepository = userPaymentRepository;
        this.userShippingRepository = userShippingRepository;
        this.passwordEncoder = passwordEncoder;
        this.publisher = publisher;
    }


    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public void createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = new Role();
        role.setRoleId(1);
        role.setName("ROLE_USER");
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, role));

        for (UserRole ur : userRoles)
            roleRepository.save(ur.getRole());

        user.getUserRoles().addAll(userRoles);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        user.setShoppingCart(shoppingCart);

        user.setUserShippingList(new ArrayList<UserShipping>());
        user.setUserPaymentList(new ArrayList<UserPayment>());

        var localUser = userRepository.save(user);
        publisher.publishEvent(new CreateUserEvent(localUser));

    }


    @Transactional
    public String createAdmin(String password) {
        var opAdmin = userRepository.findByEmail("admin");


        var admin = opAdmin.orElseGet(User::new);

        admin.setPassword(passwordEncoder.encode(password));

        if (opAdmin.isEmpty()) {
            Role role = new Role();
            role.setRoleId(1);
            role.setName("ROLE_USER");
            Role role1 = new Role();
            role1.setRoleId(2);
            role1.setName("ROLE_ADMIN");

            Set<UserRole> userRoles = new HashSet<>();

            userRoles.add(new UserRole(admin, role));
            userRoles.add(new UserRole(admin, role1));

            for (UserRole ur : userRoles)
                roleRepository.save(ur.getRole());

            admin.setUserRoles(userRoles);

            admin.setEmail("admin");
            admin.setFirstName("admin");
            admin.setEnabled(true);

            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUser(admin);
            admin.setShoppingCart(shoppingCart);
        }
            admin.setUserShippingList(new ArrayList<UserShipping>());
            admin.setUserPaymentList(new ArrayList<UserPayment>());


        userRepository.save(admin);
        return "Admin account created successfully.";

    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void updateUserBilling(UserBilling userBilling, UserPayment userPayment, User user, boolean def) {
        userPayment.setUser(user);
        userPayment.setUserBilling(userBilling);
        if (def) {
            var defaultPayment = user.getUserPaymentList().stream()
                    .filter(UserPayment::isDefaultPayment)
                    .findFirst();
            defaultPayment.ifPresent(payment -> payment.setDefaultPayment(false));
            userPayment.setDefaultPayment(true);
        }
        userBilling.setUserPayment(userPayment);
        user.getUserPaymentList().add(userPayment);
        save(user);
    }

    public void updateUserShipping(UserShipping userShipping, User user, boolean def) {
        userShipping.setUser(user);
        if (def) {
            var defaultShipping = user.getUserShippingList().stream()
                    .filter(UserShipping::isUserShippingDefault)
                    .findFirst();
            defaultShipping.ifPresent(shipping -> shipping.setUserShippingDefault(false));
            userShipping.setUserShippingDefault(true);
        }

        user.getUserShippingList().add(userShipping);
        save(user);
    }

    public void setUserDefaultPayment(Long userPaymentId, User user) {
        List<UserPayment> userPaymentList = (List<UserPayment>) userPaymentRepository.findAll();

        for (UserPayment userPayment : userPaymentList) {
            if (userPaymentId.equals(userPayment.getId())) {
                userPayment.setDefaultPayment(true);
                userPaymentRepository.save(userPayment);
            } else {
                userPayment.setDefaultPayment(false);
                userPaymentRepository.save(userPayment);
            }
        }
    }

    public void setUserDefaultShipping(Long userShippingId, User user) {
        List<UserShipping> userShippingList = (List<UserShipping>) userShippingRepository.findAll();

        for (UserShipping userShipping : userShippingList) {
            if (userShipping.getId().equals(userShippingId)) {
                userShipping.setUserShippingDefault(true);
                userShippingRepository.save(userShipping);
            } else {
                userShipping.setUserShippingDefault(false);
                userShippingRepository.save(userShipping);
            }
        }
    }

}
