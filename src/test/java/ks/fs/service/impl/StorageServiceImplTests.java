package ks.fs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.TemporaryFolder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import kn.fs.dao.FileRepository;
import kn.fs.dao.UserRepository;
import kn.fs.domain.FileItem;
import kn.fs.domain.User;
import kn.fs.security.UserDetails;
import kn.fs.service.impl.StorageServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("StorageService tests!")
public class StorageServiceImplTests {

	private static final Long USER_ID = 123L;
	private static final String TMP_FILE_NAME = "tmp.txt";

	@Mock
	private FileRepository fileRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private StorageServiceImpl storageService;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private static final User LOGGED_USER;

	static {
		LOGGED_USER = new User();
		LOGGED_USER.setId(USER_ID);
	}

	@BeforeEach
	void setMock() {
		UserDetails applicationUser = new UserDetails(LOGGED_USER);
		Authentication authentication = mock(Authentication.class);
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

		assertThrows(IllegalArgumentException.class, () -> storageService.loadAll());
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

	@Test
	@DisplayName("Test load file without any data in DB.")
	public void testLoadWithoutFile() {
		Long fileId = 321L;
		when(fileRepository.findById(fileId)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> storageService.load(fileId));
	}

	@Test
	@DisplayName("Test load file with wrong file owner.")
	public void testLoadWithWrongFileOwner() {
		Long fileId = 321L;
		User user = new User();
		FileItem fileItem = new FileItem(fileId);
		fileItem.setOwner(user);
		when(fileRepository.findById(fileId)).thenReturn(Optional.of(fileItem));
		assertThrows(AccessDeniedException.class, () -> storageService.load(fileId));
	}

	@Test
	@DisplayName("Test load file with ok file data.")
	public void testLoadWithOKFile() {
		Long fileId = 321L;
		FileItem fileItem = new FileItem(fileId);
		fileItem.setOwner(LOGGED_USER);
		when(fileRepository.findById(fileId)).thenReturn(Optional.of(fileItem));
		assertEquals(fileItem, storageService.load(fileId));
	}

	private MultipartFile mockMultipartFile() {
		MultipartFile file = mock(MultipartFile.class);
		when(file.getOriginalFilename()).thenReturn(TMP_FILE_NAME);
		return file;
	}

	@Test
	@DisplayName("Test store file with some exception, accured at storing process.")
	public void testStoreWithStoringException() {
		doThrow(IllegalStateException.class).when(fileRepository).save(any(FileItem.class));

		assertThrows(RuntimeException.class, () -> storageService.store(mockMultipartFile()));
	}

	@Test
	@DisplayName("Test store file. Positive case.")
	public void testStore() {
		// return what was passed as an argument for checking some service method logic!
		when(fileRepository.save(any(FileItem.class))).thenAnswer(answer -> answer.getArguments()[0]);

		FileItem savedFile = storageService.store(mockMultipartFile());
		assertNotNull(savedFile);
		assertEquals(TMP_FILE_NAME, savedFile.getName());
		assertEquals(LOGGED_USER, savedFile.getOwner());
		
		assertNotNull(savedFile.getFsPath());
		assertTrue(savedFile.getFsPath().startsWith("null/" + USER_ID + "/"));
	}
}
