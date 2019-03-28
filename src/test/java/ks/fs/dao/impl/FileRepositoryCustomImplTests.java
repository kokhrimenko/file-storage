package ks.fs.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import kn.fs.FileStorageApplication;
import kn.fs.dao.impl.FileRepositoryCustomImpl;
import kn.fs.domain.FileItem;
import kn.fs.domain.projections.SharedFileItem;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ContextConfiguration(classes = { FileStorageApplication.class })
@DisplayName("FileRepositoryCustomImpl DAO tests!")
@ActiveProfiles({"test"})
@SqlGroup({
    @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:dao/fileItemDataset_before.sql"),
    @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:dao/fileItemDataset_after.sql")
})
public class FileRepositoryCustomImplTests {
	private static final String FILE_NAME_FORMAT = "TC-fileName%s";
	private static final String FILE_PATH_FORMAT = "/tmp/%s/TC-fileName%s.txt";
	@Autowired
	private FileRepositoryCustomImpl fileRepository;

	@Test
	@DisplayName("Test FileRepositoryCustom.findAllByOwner - success case.")
	public void testFindAllByOwner() {
		final Long ownerId = 1L;
		List<SharedFileItem> files = fileRepository.findAllByOwner(ownerId);
		assertNotNull(files);
		assertEquals(3, files.size());
		
		long curId = 1;
		for(SharedFileItem item:files) {
			checkItem(item, ownerId, curId, false);
			curId ++;
		}
	}
	
	@Test
	@DisplayName("Test FileRepositoryCustom.findAllByOwner - empty result.")
	public void testFindAllByOwnerEmptyResult() {
		final Long ownerId = 10033L;
		List<SharedFileItem> files = fileRepository.findAllByOwner(ownerId);
		assertNotNull(files);
		assertTrue(files.isEmpty());
	}
	
	@Test
	@DisplayName("Test FileRepositoryCustom.findAllFromShared - success case.")
	public void testFindAllFromShared() {
		final Long ownerId = 1L,
				userId = 2L;
		List<SharedFileItem> files = fileRepository.findAllFromShared(userId);
		assertNotNull(files);
		assertEquals(2, files.size());
		
		long curId = 2;
		for(SharedFileItem item:files) {
			checkItem(item, ownerId, curId, true);
			curId ++;
		}
	}
	
	@Test
	@DisplayName("Test FileRepositoryCustom.findAllFromShared - empty result.")
	public void testFindAllFromSharedrEmptyResult() {
		final Long userId = 10033L;
		List<SharedFileItem> files = fileRepository.findAllFromShared(userId);
		assertNotNull(files);
		assertTrue(files.isEmpty());
	}
	
	@Test
	@DisplayName("Test FileRepositoryCustom.findByIdAndOwner - success case.")
	public void testFindByIdAndOwner() {
		final Long ownerId = 4L, fileId = 34L;
		Optional<FileItem> file = fileRepository.findByIdAndOwner(ownerId, fileId);
		assertNotNull(file);
		assertTrue(file.isPresent());
		checkItem(file.get(), ownerId, fileId);
	}
	
	@Test
	@DisplayName("Test FileRepositoryCustom.findByIdAndOwner -  empty result.")
	public void testFindByIdAndOwnerEmptyResult() {
		final Long ownerId = 2L, fileId = 34L;
		Optional<FileItem> file = fileRepository.findByIdAndOwner(ownerId, fileId);
		assertNotNull(file);
		assertFalse(file.isPresent());
	}
	
	private void checkItem(SharedFileItem item, Long ownerId, Long id, boolean isShared) {
		assertNotNull(item);
		checkItem(item.getFileItem(), ownerId, id);
		assertEquals(isShared, item.isShared());
	}
	
	private void checkItem(FileItem item, Long ownerId, Long id) {
		assertNotNull(item);
		assertNotNull(item.getOwner());
		assertEquals(ownerId, item.getOwner().getId());
		assertEquals(id, item.getId());
		assertEquals(String.format(FILE_NAME_FORMAT, id), item.getName());
		assertEquals(String.format(FILE_PATH_FORMAT, ownerId, id), item.getFsPath());
	}
}
