package com.garden.treehouse.repos;

import com.garden.treehouse.model.security.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
	Optional<Role> findByName(String name);
}
