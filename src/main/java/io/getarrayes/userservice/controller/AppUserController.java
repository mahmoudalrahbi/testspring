package io.getarrayes.userservice.controller;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.getarrayes.userservice.model.AppRole;
import io.getarrayes.userservice.model.AppUser;
import io.getarrayes.userservice.service.AppUserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor 
@Slf4j
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
	
	@GetMapping("/token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws StreamWriteException, DatabindException, IOException
	{
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if(authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
		{
			try {
				String token = authorizationHeader.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(token);
				String username = decodedJWT.getSubject();
				AppUser user = userService.getUser(username);
				String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
				Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
				
				for (String role : roles) {
					authorities.add(new SimpleGrantedAuthority(role));
				}
				
				
				String access_token = JWT.create()
						.withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 100 ))
						.withIssuer(request.getRequestURI().toString())
						.withClaim(
								"roles", 
								user.getRoles().stream().map(AppRole::getName).collect(Collectors.toList()))
						.sign(algorithm);
			
			
			
			String refresh_token = JWT.create()
					.withSubject(user.getUsername())
					.withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 100 ))
					.withIssuer(request.getRequestURI().toString())
					.sign(algorithm);
			
//			response.setHeader("access_token", access_token);
//			response.setHeader("refresh_token", refresh_token); 
			
			Map<String, String> tokens = new HashMap();
			tokens.put("access_token", access_token);
			tokens.put("refresh_token", refresh_token);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			new ObjectMapper().writeValue(response.getOutputStream(), tokens);
			
//				filterChain.doFilter(request, response);
			}catch(Exception e){
				log.error("Error logging in: {}",e.getMessage());
				response.setHeader("error", e.getMessage());
				response.setStatus(HttpStatus.FORBIDDEN.value()) ;
//				response.sendError(HttpStatus.FORBIDDEN.value());
				
				Map<String, String> errors = new HashMap();
				errors.put("error_message", e.getMessage());
//				tokens.put("refresh_token", refresh_token);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), errors);
				
			}
		}else {
			throw new RuntimeException("Refresh token is missing");
		}
	}
	
}

@Data
class RoleToUserForm{
	private String username;
	private String rolename;
}
