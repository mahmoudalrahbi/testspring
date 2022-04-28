package io.getarrayes.userservice.service;

import java.util.List;

import io.getarrayes.userservice.model.AppRole;
import io.getarrayes.userservice.model.AppUser;

public interface AppUserService {
	AppUser saveUser(AppUser user);
	AppRole saveRole(AppRole role);
	void addRoleToUser(String username, String roleName);
	AppUser getUser(String username);
	List<AppUser> getUsers();
}
