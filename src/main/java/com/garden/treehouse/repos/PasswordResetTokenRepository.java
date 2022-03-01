package com.garden.treehouse.repos;

import com.garden.treehouse.model.User;
import com.garden.treehouse.model.security.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
	
	Optional<PasswordResetToken> findByToken(String token);
	
	Optional<PasswordResetToken> findByUser(User user);
	
	Optional<Stream<PasswordResetToken>> findAllByExpiryDateLessThan(Date now);
	
	@Modifying
	@Query("delete from PasswordResetToken t where t.expiryDate <= ?1")
	void deleteAllExpiredSince(Date now);

}
