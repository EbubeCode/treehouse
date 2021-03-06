package com.garden.treehouse.services;

import com.garden.treehouse.model.ShoppingCart;
import com.garden.treehouse.model.User;
import com.garden.treehouse.model.UserShipping;
import com.garden.treehouse.model.security.Role;
import com.garden.treehouse.model.security.UserRole;
import com.garden.treehouse.repos.RoleRepository;
import com.garden.treehouse.repos.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher publisher;


    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ApplicationEventPublisher publisher) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
        role = roleRepository.save(role);
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, role));


        user.setUserRoles(userRoles);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        user.setShoppingCart(shoppingCart);

        user.setUserShippingList(new ArrayList<UserShipping>());
        userRepository.save(user);

    }


    @Transactional
    public String createAdmin(String password) {

        var admin = findByEmail("admin");
        if (admin != null)
            userRepository.delete(admin);
        admin = new User();

        admin.setPassword(passwordEncoder.encode(password));

        Role role = new Role();
        role.setRoleId(1);
        role.setName("ROLE_USER");
        role = roleRepository.save(role);
        Role role1 = new Role();
        role1.setRoleId(2);
        role1.setName("ROLE_ADMIN");
        role1 = roleRepository.save(role1);

        Set<UserRole> userRoles = new HashSet<>();

        userRoles.add(new UserRole(admin, role));
        userRoles.add(new UserRole(admin, role1));


        admin.setUserRoles(userRoles);

        admin.setEmail("admin");
        admin.setFirstName("admin");
        admin.setEnabled(true);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(admin);
        admin.setShoppingCart(shoppingCart);

        admin.setUserShippingList(new ArrayList<UserShipping>());


        userRepository.save(admin);
        return "Admin account created successfully.";

    }


    public User save(User user) {
        return userRepository.save(user);
    }


    public void updateUserShipping(UserShipping userShipping, User user, boolean def) {
        userShipping.setUser(user);
        if (def) {
            var defaultShipping = user.getUserShippingList().stream().filter(UserShipping::isUserShippingDefault).findFirst();
            defaultShipping.ifPresent(shipping -> shipping.setUserShippingDefault(false));
            userShipping.setUserShippingDefault(true);
        }

        user.getUserShippingList().add(userShipping);
        save(user);
    }


}
