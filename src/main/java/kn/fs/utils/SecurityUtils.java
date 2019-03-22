package kn.fs.utils;

import java.security.Principal;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import kn.fs.security.UserDetails;

public final class SecurityUtils {

	private SecurityUtils() {
		super();
	}

	public static Long getLoggedUserId(Principal principal) {
		return ((UserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUserId();
	}

}
