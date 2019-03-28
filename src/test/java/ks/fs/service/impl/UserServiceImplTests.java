package ks.fs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import org.springframework.security.access.AccessDeniedException;

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
	@DisplayName("Test UserService.loadAll - users except current user.")
	public void testLoadAllUsersExceptCurrentOne() {
		int countOfUsers = 11;
		Long userId = 33L;
		List<User> users = LongStream.range(0, countOfUsers).mapToObj(index -> new User(index))
				.collect(Collectors.toList());

		when(userRepository.findAllExceptOf(userId)).thenReturn(users);
		when(userRepository.findAllExceptOf(userId + 1)).thenReturn(null);

		List<User> usersList = userService.loadAllExceptCurrent(userId);
		assertNotNull(usersList);
		assertEquals(users, usersList);

		usersList = userService.loadAllExceptCurrent(userId + 1);
		assertNull(usersList);
	}

	@Test
	@DisplayName("Test UserService.share - without user.")
	public void testShareWithoutUser() {
		Long userId = 33L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> userService.share(userId, 1L, true));
	}

	@Test
	@DisplayName("Test UserService.share - without file.")
	public void testShareWithoutFile() {
		Long fileId = 33L,
				userId = 44L;
		when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId)));
		when(fileRepository.findById(fileId)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> userService.share(userId, fileId, true));
	}

	@Test
	@DisplayName("Test UserService.delete - success case.")
	public void testDeleteShare() {
		Long fileId = 33L,
				userId = 44L;
		userService.share(userId, fileId, false);
		
		verify(userShareRepository, times(1)).deleteById(new UserShareFileId(userId, fileId));
		verify(userShareRepository, times(0)).save(any(UserShareFile.class));
	}
	
	@Test
	@DisplayName("Test UserService.share - success case.")
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
	
	@Test
	@DisplayName("Test UserService.loadUserByUsername - without user.")
	public void testLoadUserByUsernameWithoutUser() {
		String userName = "tested user name";
		when(userRepository.findByUsername(userName)).thenReturn(Optional.empty());
		
		User user = userService.loadUserByUsername(userName);
		assertNull(user);
	}
	
	@Test
	@DisplayName("Test UserService.loadUserByUsername - success case.")
	public void testLoadUserByUsername() {
		String userName = "tested user name";
		User testedUser = new User(1L, userName);
		when(userRepository.findByUsername(userName)).thenReturn(Optional.of(testedUser));
		
		User user = userService.loadUserByUsername(userName);
		assertNotNull(user);
		assertEquals(testedUser, user);
	}
	
	@Test
	@DisplayName("Test UserService.createUser - null as a user.")
	public void testCreateUserWithNullUser() {
		assertThrows(IllegalArgumentException.class, () -> userService.createUser(null));
	}
	
	@Test
	@DisplayName("Test UserService.createUser - success case.")
	public void testCreateUserWith() {
		User user = new User();
		userService.createUser(user);

		verify(userRepository, times(1)).save(user);
	}
	
	@Test
	@DisplayName("Test UserService.loadAlreadySharedList - wrong fileId.")
	public void testLoadAlreadySharedListWithWrongFileId() {
		long fileId = 31l;

		when(fileRepository.findById(fileId)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> userService.loadAlreadySharedList(fileId, anyLong()));
	}

	@Test
	@DisplayName("Test UserService.loadAlreadySharedList - file, owned by not currently logged-in user.")
	public void testLoadAlreadySharedListOnFileWithAnotherOwner() {
		long fileId = 31l,
				loggedUserId = 13l;
		FileItem fileItem = new FileItem(fileId);
		fileItem.setOwner(new User(loggedUserId + 1));

		when(fileRepository.findById(fileId)).thenReturn(Optional.of(fileItem));
		assertThrows(AccessDeniedException.class, () -> userService.loadAlreadySharedList(fileId, loggedUserId));
	}

	@Test
	@DisplayName("Test UserService.loadAlreadySharedList - file with null owner.")
	public void testLoadAlreadySharedListOnFileWithNullOwner() {
		long fileId = 31l;
		FileItem fileItem = new FileItem(fileId);
		fileItem.setOwner(null);

		when(fileRepository.findById(fileId)).thenReturn(Optional.of(fileItem));
		assertThrows(AccessDeniedException.class, () -> userService.loadAlreadySharedList(fileId, anyLong()));
	}
	
	@Test
	@DisplayName("Test UserService.loadAlreadySharedList - success case.")
	public void testLoadAlreadySharedList() {
		long fileId = 31l,
				loggedUserId = 13l;
		FileItem fileItem = new FileItem(fileId);
		fileItem.setOwner(new User(loggedUserId));
		int countOfUsers = 3;
		List<User> users = LongStream.range(0, countOfUsers).mapToObj(id -> new User(id + 1))
				.collect(Collectors.toList());

		when(fileRepository.findById(fileId)).thenReturn(Optional.of(fileItem));
		when(userRepository.listUserBySharedFile(fileId)).thenReturn(users);
		List<User> storedUsersList = userService.loadAlreadySharedList(fileId, loggedUserId);
		assertNotNull(storedUsersList);
		assertEquals(users, storedUsersList);
	}
}