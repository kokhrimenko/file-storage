package ks.fs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import kn.fs.dao.FileRepository;
import kn.fs.dao.UserRepository;
import kn.fs.domain.FileItem;
import kn.fs.domain.User;
import kn.fs.domain.projections.SharedFileItem;
import kn.fs.service.impl.StorageServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("StorageService tests!")
@EnableRuleMigrationSupport
public class StorageServiceImplTests {

	private static final Long USER_ID = 123L;
	private static final String TMP_FILE_NAME = "tmp.txt";
	private static final User LOGGED_USER;

	static {
		LOGGED_USER = new User();
		LOGGED_USER.setId(USER_ID);
	}

	@Mock
	private FileRepository fileRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private StorageServiceImpl storageService;

	@Rule
	private TemporaryFolder folder = new TemporaryFolder();

	@BeforeEach
	void setMock() throws IOException {
		folder.create();
		ReflectionTestUtils.setField(storageService, "storePath", getBaseFolder());
	}

	private String getBaseFolder() {
		return folder.getRoot().getAbsolutePath();
	}

	@Test
	@DisplayName("Test loadAll method with empty result.")
	public void testLoadAllWithEmptyResult() {
		assertEquals(Collections.emptyList(), storageService.loadAll(USER_ID));
	}

	@Test
	@DisplayName("Test loadAll method with data.")
	public void testLoadAllWithData() {
		int countOfItems = 10;
		List<SharedFileItem> items = IntStream.range(0, countOfItems)
				.mapToObj(index -> new SharedFileItem(new FileItem(new Long(index)), false))
				.collect(Collectors.toList());
		when(fileRepository.findAllByOwner(USER_ID)).thenReturn(items);
		assertEquals(items, storageService.loadAll(USER_ID));
	}

	@Test
	@DisplayName("Test load file without any data in DB.")
	public void testLoadWithoutFile() {
		Long fileId = 321L;
		when(fileRepository.findByIdAndOwner(USER_ID, fileId)).thenReturn(Optional.empty());
		assertThrows(AccessDeniedException.class, () -> storageService.load(fileId, USER_ID));
	}

	@Test
	@DisplayName("Test load file with ok file data.")
	public void testLoadWithOKFile() {
		Long fileId = 321L;
		FileItem fileItem = new FileItem(fileId);
		fileItem.setOwner(LOGGED_USER);

		when(fileRepository.findByIdAndOwner(USER_ID, fileId)).thenReturn(Optional.of(fileItem));

		assertEquals(fileItem, storageService.load(fileId, USER_ID));
	}

	private MultipartFile mockMultipartFile() {
		MultipartFile file = mock(MultipartFile.class);
		when(file.getOriginalFilename()).thenReturn(TMP_FILE_NAME);
		return file;
	}

	@Test
	@DisplayName("Test init service without provided path.")
	public void testInitWithoutPath() {
		StorageServiceImpl realStorageService = new StorageServiceImpl();
		assertThrows(IllegalArgumentException.class, () -> realStorageService.init());
	}

	@Test
	@DisplayName("Test init service with wrong path.")
	public void testInitWithWrongPath() {
		StorageServiceImpl realStorageService = new StorageServiceImpl();
		ReflectionTestUtils.setField(realStorageService, "storePath", "/" + UUID.randomUUID().toString());
		assertThrows(IllegalArgumentException.class, () -> realStorageService.init());
	}

	@Test
	@DisplayName("Test init service with path, pointed to a file.")
	public void testInitWithPathPointedToFile() throws IOException {
		File file = folder.newFile(UUID.randomUUID().toString());
		StorageServiceImpl realStorageService = new StorageServiceImpl();
		ReflectionTestUtils.setField(realStorageService, "storePath", file.getAbsolutePath());
		assertThrows(IllegalArgumentException.class, () -> realStorageService.init());
	}

	@Test
	@DisplayName("Test init service with correct path.")
	public void testInitWithCorrectPath() throws IOException {
		StorageServiceImpl realStorageService = new StorageServiceImpl();
		ReflectionTestUtils.setField(realStorageService, "storePath", folder.getRoot().getAbsolutePath());
	}

	@Test
	@DisplayName("Test init service wit empty path.")
	public void testInitWitEmptyPath() {
		StorageServiceImpl realStorageService = new StorageServiceImpl();
		ReflectionTestUtils.setField(realStorageService, "storePath", "");
		assertThrows(IllegalArgumentException.class, () -> realStorageService.init());
	}

	@Test
	@DisplayName("Test store file without existed user.")
	public void testStoreWithoutUser() {
		when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> storageService.store(mockMultipartFile(), USER_ID));
	}

	@Test
	@DisplayName("Test store file with some exception, occured at storing process.")
	public void testStoreWithStoringException() {
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(LOGGED_USER));
		doThrow(IllegalStateException.class).when(fileRepository).save(any(FileItem.class));

		assertThrows(RuntimeException.class, () -> storageService.store(mockMultipartFile(), USER_ID));
	}

	@Test
	@DisplayName("Test store file. Positive case.")
	public void testStore() {
		// return what was passed as an argument for checking some service method logic!
		when(fileRepository.save(any(FileItem.class))).thenAnswer(answer -> answer.getArguments()[0]);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(LOGGED_USER));

		FileItem savedFile = storageService.store(mockMultipartFile(), USER_ID);
		assertNotNull(savedFile);
		assertEquals(TMP_FILE_NAME, savedFile.getName());
		assertEquals(LOGGED_USER, savedFile.getOwner());

		assertNotNull(savedFile.getFsPath());
		assertTrue(savedFile.getFsPath().startsWith(getBaseFolder() + "/" + USER_ID + "/"));
	}
}
