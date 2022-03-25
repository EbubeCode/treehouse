package com.garden.treehouse.utility;

import com.garden.treehouse.model.Order;
import com.garden.treehouse.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.InternetAddress;
import java.util.Locale;


@Component
public class MailConstructor {

    private final TemplateEngine templateEngine;

    @Value("${base_url}")
    private String baseUrl;

    public MailConstructor(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public SimpleMailMessage constructResetTokenEmail(
            String token, User user
    ) {

        var userEmail = user.getEmail();
        String url = baseUrl + "/verify?token_id=" + token + "&is_password=false";
        String message = """
                Please click on the above link to verify your email and start placing your orders.
                 Note that this email will expire within 24 hours.""";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(userEmail);
        email.setSubject("Tree-House - New User");
        email.setText(url + "\n" + message);
        return email;

    }

    public SimpleMailMessage creatEmailUserForPasswordReset(
            String token, String userEmail
    ) {

        String url = baseUrl + "/verify?token_id=" + token + "&is_password=true";
        String message = """
                Please click on the above link to verify your email and reset your password.
                Note that this email will expire within 24 hours.""";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(userEmail);
        email.setSubject("Tree-House - Change Password");
        email.setText(url + "\n" + message);
        return email;

    }

    public MimeMessagePreparator constructOrderConfirmationEmail(User user, Order order, Locale locale) {
        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("user", user);
        context.setVariable("cartItems", order.getCartItemList());
        String text = templateEngine.process("orderConfirmationEmailTemplate", context);

        return mimeMessage -> {
            MimeMessageHelper email = new MimeMessageHelper(mimeMessage);
            email.setTo(user.getEmail());
            email.setSubject("Order Confirmation - " + order.getId());
            email.setText(text, true);
            email.setFrom(new InternetAddress("foysal.ecommerce@gmail.com"));
        };
    }


}
