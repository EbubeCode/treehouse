package com.garden.treehouse;

import com.garden.treehouse.model.Payment;
import com.garden.treehouse.repos.PaymentRepository;
import com.garden.treehouse.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
@ServletComponentScan
public class TreehouseApplication  implements CommandLineRunner {

    @Value("${admin-password}")
    private String adminPassword;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(TreehouseApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        userService.createAdmin(adminPassword);

        var payments = paymentRepository.findAll();
        if (payments.isEmpty()) {
            var payment1 = new Payment("Master Card", "5531886652142950", 9, 30, 564);
            var payment2 = new Payment("Visa", "4187427415564246", 5, 23, 828);
            var payment3 = new Payment("Verve", "5061460410120223210", 3, 27, 780);

            System.out.println(paymentRepository.saveAll(List.of(payment1, payment2, payment3)));


        }
    }
}
