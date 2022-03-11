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
import javax.mail.internet.MimeMessage;
import java.util.Locale;


@Component
public class MailConstructor {

    private final TemplateEngine templateEngine;

    public MailConstructor(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public SimpleMailMessage constructResetTokenEmail(
            String contextPath, String token, User user
    ) {

        String url = contextPath + "/verify?token_id=" + token;
        String message = "\nPlease click on this link to verify your email and edit your personal information.\n";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Tree-House - New User");
        email.setText(url + message);
        return email;

    }

    public MimeMessagePreparator constructOrderConfirmationEmail(User user, Order order, Locale locale) {
        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("user", user);
        context.setVariable("cartItemList", order.getCartItemList());
        String text = templateEngine.process("orderConfirmationEmailTemplate", context);

        return new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper email = new MimeMessageHelper(mimeMessage);
                email.setTo(user.getEmail());
                email.setSubject("Order Confirmation - " + order.getId());
                email.setText(text, true);
                email.setFrom(new InternetAddress("foysal.ecommerce@gmail.com"));
            }
        };
    }


}
