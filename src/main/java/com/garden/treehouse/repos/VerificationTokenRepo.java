package com.garden.treehouse.repos;

import com.garden.treehouse.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepo extends JpaRepository<VerificationToken, String> {
}
