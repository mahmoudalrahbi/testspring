package io.getarrayes.userservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.getarrayes.userservice.model.AppUser;

@Repository
public interface AppUserRepo extends JpaRepository<AppUser, Long>{
	AppUser findByUsername(String username);
}
