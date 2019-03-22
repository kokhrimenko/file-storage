package kn.fs.service.test;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import kn.fs.domain.User;

@Service(value = "TestUserDetailsService")
@Profile("test")
public class UserDetailsServiceImplTest implements UserDetailsService{
	public static final Long TEST_USER_ID = 33L;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = new User(TEST_USER_ID);

		return new kn.fs.security.UserDetails(user);
	}
}
