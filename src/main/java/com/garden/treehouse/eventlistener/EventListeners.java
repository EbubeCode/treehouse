package com.garden.treehouse.eventlistener;

import com.garden.treehouse.events.CreateUserEvent;
import com.garden.treehouse.events.ForgotPasswordEvent;
import com.garden.treehouse.model.VerificationToken;
import com.garden.treehouse.repos.VerificationTokenRepo;
import com.garden.treehouse.services.UserService;
import com.garden.treehouse.utility.MailConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Component
public class EventListeners {
    private final JavaMailSender mailSender;
    private final MailConstructor mailConstructor;
    private final UserService userService;
    private final VerificationTokenRepo tokenRepo;


    public EventListeners(JavaMailSender mailSender, MailConstructor mailConstructor, UserService userService, VerificationTokenRepo tokenRepo) {
        this.mailSender = mailSender;
        this.mailConstructor = mailConstructor;
        this.userService = userService;
        this.tokenRepo = tokenRepo;
    }

    @EventListener
    public void createUserEvent(CreateUserEvent userEvent) {
        var user = userEvent.user();
        var verificationToken = new VerificationToken();
        verificationToken.setUserEmail(user.getEmail());
        verificationToken.setExpiryDate(addHoursToDate(new Date(System.currentTimeMillis()), 24));
        verificationToken.setTokenId(UUID.randomUUID().toString());
        verificationToken = tokenRepo.save(verificationToken);
        var tokenId = verificationToken.getTokenId();

        SimpleMailMessage email = mailConstructor.constructResetTokenEmail(tokenId, user);

        mailSender.send(email);

    }

    private Date addHoursToDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    @EventListener
    public void forgotPasswordEvent(ForgotPasswordEvent event) {
        var userEmail = event.email();

        var verificationToken = new VerificationToken();
        verificationToken.setUserEmail(userEmail);
        verificationToken.setExpiryDate(addHoursToDate(new Date(System.currentTimeMillis()), 24));
        verificationToken.setTokenId(UUID.randomUUID().toString());
        verificationToken = tokenRepo.save(verificationToken);
        var tokenId = verificationToken.getTokenId();

        SimpleMailMessage email = mailConstructor.creatEmailUserForPasswordReset(tokenId, userEmail);

        mailSender.send(email);

    }
}
