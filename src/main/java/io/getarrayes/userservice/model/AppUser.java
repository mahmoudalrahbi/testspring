package io.getarrayes.userservice.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Data 
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class AppUser {
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	public String name;
	public String username;
	public String password;
	@ManyToMany(fetch =FetchType.EAGER)
	public Collection<AppRole> roles = new ArrayList<AppRole>();
}
