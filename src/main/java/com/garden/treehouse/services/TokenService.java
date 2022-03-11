package com.garden.treehouse.services;

import com.garden.treehouse.model.enums.VerifyTokenStates;
import com.garden.treehouse.repos.VerificationTokenRepo;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public record TokenService(VerificationTokenRepo tokenRepo,
                           UserService userService) {

    public VerifyTokenStates verifyToken(String tokenId) {
        var optionalToken = tokenRepo.findById(tokenId);

        if (optionalToken.isPresent()) {
            var token = optionalToken.get();
            tokenRepo.delete(token);
            var presentDate = new Date(System.currentTimeMillis());
            if (presentDate.after(token.getExpiryDate())) {
                return VerifyTokenStates.EXPIRED_TOKEN;
            }
            var user = userService.findByEmail(token.getUsername());
            if (user == null) {
                return VerifyTokenStates.INVALID_TOKEN;
            }
            user.setEnabled(true);
            userService.save(user);
            return VerifyTokenStates.VALID_TOKEN;
        }
        return VerifyTokenStates.INVALID_TOKEN;
    }


}
