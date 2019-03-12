package ks.fs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import kn.fs.dao.FileRepository;
import kn.fs.dao.UserRepository;
import kn.fs.domain.FileItem;
import kn.fs.domain.User;
import kn.fs.security.UserDetails;
import kn.fs.service.impl.StorageServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Spring boot 2 mockito2 Junit5 example")
public class StorageServiceImplTests {

	private static final long USER_ID = 123L;

	@Mock
	private FileRepository fileRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private StorageServiceImpl storageService;
	
	private static final User LOGGED_USER;
	
	static {
		LOGGED_USER = new User();
		LOGGED_USER.setId(USER_ID);
	}
	
	@BeforeEach
	void setMock() {
		UserDetails applicationUser = new UserDetails(LOGGED_USER);
        Authentication authentication =  mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
		
		when(userRepository.findById(USER_ID)).thenReturn(LOGGED_USER);
	}
	
	@Test
	@DisplayName("Test loadAll method with empty result.")
	public void testLoadAllWithEmptyResult() {
		assertEquals(Collections.emptyList(), storageService.loadAll());
	}
	
	@Test
	@DisplayName("Test loadAll method with empty result.")
	public void testLoadAllWithoutLoggedUser() {
		when(userRepository.findById(USER_ID)).thenReturn(null);
		
		assertThrows(IllegalArgumentException.class, () -> assertEquals(Collections.emptyList(), storageService.loadAll()));
	}
	
	@Test
	@DisplayName("Test loadAll method with data.")
	public void testLoadAllWithData() {
		int countOfItems = 10;
		List<FileItem> items = IntStream.range(0, countOfItems).mapToObj(index -> new FileItem(new Long(index)))
					.collect(Collectors.toList());
		when(fileRepository.findAllByOwner(LOGGED_USER)).thenReturn(items);
		assertEquals(items, storageService.loadAll());
	}
	
	
}
