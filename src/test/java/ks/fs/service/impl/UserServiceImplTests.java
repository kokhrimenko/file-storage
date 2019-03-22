package ks.fs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kn.fs.dao.FileRepository;
import kn.fs.dao.UserRepository;
import kn.fs.dao.UserShareRepository;
import kn.fs.domain.FileItem;
import kn.fs.domain.User;
import kn.fs.domain.UserShareFile;
import kn.fs.domain.pk.UserShareFileId;
import kn.fs.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService tests!")
public class UserServiceImplTests {

	@Mock
	private UserRepository userRepository;
	@Mock
	private FileRepository fileRepository;
	@Mock
	private UserShareRepository userShareRepository;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	@DisplayName("Test loadAll users except current user.")
	public void testLoadAllUsersExceptCurrentOne() {
		int countOfUsers = 11;
		Long userId = 33L;
		List<User> users = LongStream.range(0, countOfUsers).mapToObj(index -> new User(index))
				.collect(Collectors.toList());

		when(userRepository.findAllExcectOf(userId)).thenReturn(users);
		when(userRepository.findAllExcectOf(userId + 1)).thenReturn(null);

		List<User> usersList = userService.loadAllExceptCurrent(userId);
		assertNotNull(usersList);
		assertEquals(users, usersList);

		usersList = userService.loadAllExceptCurrent(userId + 1);
		assertNull(usersList);
	}

	@Test
	@DisplayName("Test share without user.")
	public void testShareWithoutUser() {
		Long userId = 33L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> userService.share(userId, 1L, true));
	}

	@Test
	@DisplayName("Test share without file.")
	public void testShareWithoutFile() {
		Long fileId = 33L,
				userId = 44L;
		when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId)));
		when(fileRepository.findById(fileId)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> userService.share(userId, fileId, true));
	}

	@Test
	@DisplayName("Test deete share.")
	public void testDeleteShare() {
		Long fileId = 33L,
				userId = 44L;
		userService.share(userId, fileId, false);
		
		verify(userShareRepository, times(1)).deleteById(new UserShareFileId(userId, fileId));
		verify(userShareRepository, times(0)).save(any(UserShareFile.class));
	}
	
	@Test
	@DisplayName("Test share.")
	public void testShare() {
		Long fileId = 33L,
				userId = 44L;
		User user = new User(userId);
		FileItem file = new FileItem(fileId);
		
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
		userService.share(userId, fileId, true);
		
		verify(userShareRepository, times(0)).deleteById(new UserShareFileId(userId, fileId));
		verify(userShareRepository, times(1)).save(any(UserShareFile.class));
	}
}