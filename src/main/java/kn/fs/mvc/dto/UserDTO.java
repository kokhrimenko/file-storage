package kn.fs.mvc.dto;

import kn.fs.domain.User;

public class UserDTO {

	private Long id;
	private String name;

	public UserDTO(User user) {
		super();
		if(user == null) {
			throw new IllegalAccessError("User can't be NULL!");
		}
	
		this.id = user.getId();
		this.name = user.getUsername();
	}
	
	public Long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	
	
}
