package io.getarrayes.userservice.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.getarrayes.userservice.model.AppRole;
import io.getarrayes.userservice.model.AppUser;
import io.getarrayes.userservice.service.AppUserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor 
public class AppUserController {
	private final AppUserService userService;
	
	@GetMapping("/users")
	public ResponseEntity<List<AppUser>> getUsers()
	{
		return ResponseEntity.ok().body(userService.getUsers());
	}
	
	@PostMapping("/users")
	public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user)
	{
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user").toUriString());
		return ResponseEntity.created(uri).body(userService.saveUser(user));
	}
	
	
	@PostMapping("/roles")
	public ResponseEntity<AppRole> saveRole(@RequestBody AppRole role)
	{
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role").toUriString());
		return ResponseEntity.created(uri).body(userService.saveRole(role));
	}
	
	
	@PostMapping("/roles/users")
	public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form)
	{
		userService.addRoleToUser(form.getUsername(), form.getRolename());
		return ResponseEntity.ok().build();
	}
}

@Data
class RoleToUserForm{
	private String username;
	private String rolename;
}
