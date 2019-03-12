package ks.fs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import kn.fs.dao.UserRepository;
import kn.fs.domain.User;
import kn.fs.service.impl.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsService tests!")
public class UserDetailsServiceImplTests {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserDetailsServiceImpl userDetailsService;

	@Test
	@DisplayName("Test loadUserByUsername with wrong username.")
	public void testLoadUserByUsernameWithWrongName() {
		String userName = "test";
		when(userRepository.findByUsername(userName)).thenReturn(null);
		assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(userName));
	}
	
	@Test
	@DisplayName("Test loadUserByUsername positive case.")
	public void testLoadUserByUsername() {
		String userName = "test",
				pass = "pass";
		User user = new User();
		user.setId(1L);
		user.setUsername(userName);
		user.setPassword(pass);
		
		when(userRepository.findByUsername(userName)).thenReturn(user);
		UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
		assertNotNull(userDetails);
		assertEquals(userName, userDetails.getUsername());
		assertEquals(pass, userDetails.getPassword());
		assertTrue(userDetails.isCredentialsNonExpired());
		assertTrue(userDetails.isAccountNonLocked());
		assertTrue(userDetails.isAccountNonExpired());
		assertTrue(userDetails.isEnabled());
		
		
	}
}
