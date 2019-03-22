package kn.fs.mvc.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import kn.fs.domain.User;

public class UserRegistrationDTO {
	private static final String NO_PASSWORD = "{noop}";
	@NotNull
	@Size(min=1, max=20)
	private String username;
	@NotNull
	@Size(min=8, max=20)
	private String password;
	@NotNull
	@Size(min=8, max=20)
	private String confirmPassword;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	@Override
	public String toString() {
		return "UserRegistrationDTO [username=" + username + ", password=" + password + ", confirmPassword="
				+ confirmPassword + "]";
	}
	
	public User toUser() {
		User user = new User();
		user.setUsername(username);
		user.setPassword(NO_PASSWORD + password);
		return user;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((confirmPassword == null) ? 0 : confirmPassword.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserRegistrationDTO other = (UserRegistrationDTO) obj;
		if (confirmPassword == null) {
			if (other.confirmPassword != null)
				return false;
		} else if (!confirmPassword.equals(other.confirmPassword))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}	
}
