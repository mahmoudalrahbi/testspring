package io.getarrayes.userservice.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.getarrayes.userservice.model.AppRole;
import io.getarrayes.userservice.model.AppUser;
import io.getarrayes.userservice.repo.AppRoleRepo;
import io.getarrayes.userservice.repo.AppUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Transactional 
@Slf4j 
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService, UserDetailsService{
	
	private final AppUserRepo appUserRepo;
	private final AppRoleRepo appRoleRepo;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		AppUser user = appUserRepo.findByUsername(username);
		
		if(user == null) {
			log.error("user not found in the database");
			throw new UsernameNotFoundException("user not found in the database");
		}else {
			log.info("user found in the database: {}", username);
		}
		
		Collection<SimpleGrantedAuthority> authorites = new ArrayList<>();
		user.getRoles().forEach(role -> { 
			authorites.add(new SimpleGrantedAuthority(role.getName()));
		});
		return new User(user.getUsername(), user.getPassword(), authorites);
	}

	@Override
	public AppUser saveUser(AppUser user) {
		// TODO Auto-generated method stub
		log.info("save user: {}", user.toString());
		return appUserRepo.save(user);
	}

	@Override
	public AppRole saveRole(AppRole role) {
		// TODO Auto-generated method stub
		log.info("save role: {}", role.toString());
		return appRoleRepo.save(role);
	}

	@Override
	public void addRoleToUser(String username, String roleName) {
		// TODO Auto-generated method stub
		log.info("addRoleToUser role: {}", username);
		AppUser user = appUserRepo.findByUsername(username);
		AppRole role = appRoleRepo.findByname(roleName);
		
		user.getRoles().add(role);
	}

	@Override
	public AppUser getUser(String username) {
		// TODO Auto-generated method stub
		
		log.info("getUser username{}", username);
		return appUserRepo.findByUsername(username);
	}

	@Override
	public List<AppUser> getUsers() {
		// TODO Auto-generated method stub
		
		log.info("getUsers");
		return appUserRepo.findAll();
	}

	


}
