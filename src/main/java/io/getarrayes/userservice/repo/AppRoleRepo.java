package io.getarrayes.userservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.getarrayes.userservice.model.AppRole;

@Repository
public interface AppRoleRepo extends JpaRepository<AppRole, Long>{
	AppRole findByname(String name);
}
