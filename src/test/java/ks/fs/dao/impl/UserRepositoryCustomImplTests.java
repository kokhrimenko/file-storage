package ks.fs.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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
import kn.fs.dao.impl.UserRepositoryCustomImpl;
import kn.fs.domain.User;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ContextConfiguration(classes = { FileStorageApplication.class })
@DisplayName("UserRepositoryCustomImpl DAO tests!")
@ActiveProfiles({"test"})
@SqlGroup({
    @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:dao/userDataset_before.sql"),
    @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:dao/userDataset_after.sql")
})
public class UserRepositoryCustomImplTests {
	private static final String USERNAME_FORMAT = "TC-user%s";
	private static final String PASS_FORMAT = "TC-pass%s";
	@Autowired
	private UserRepositoryCustomImpl userRepository;

	@Test
	@DisplayName("Test UserRepositoryCustom.findAllExceptOf - success case.")
	public void testFindAllExceptOf() {
		final Long userId = 1L;
		List<User> users = userRepository.findAllExceptOf(userId);
		assertNotNull(users);
		assertEquals(2, users.size());
		
		long curId = 2;
		for(User item:users) {
			checkItem(item, curId);
			curId ++;
		}
	}
	
	@Test
	@DisplayName("Test UserRepositoryCustom.findAllExceptOf - empty result.")
	@Sql(scripts = "classpath:dao/userDataset_after.sql")
	public void testFindAllExceptOfEmptyResult() {
		final Long userId = 1L;
		List<User> users = userRepository.findAllExceptOf(userId);
		assertNotNull(users);
		assertTrue(users.isEmpty());
	}
	
	@Test
	@DisplayName("Test UserRepositoryCustom.listUserBySharedFile - success case.")
	public void testListUserBySharedFile() {
		final Long fileId = 2L;
		List<User> users = userRepository.listUserBySharedFile(fileId);
		assertNotNull(users);
		assertEquals(2, users.size());
		
		long curId = 2;
		for(User item:users) {
			checkItem(item, curId);
			curId ++;
		}
	}
	
	@Test
	@DisplayName("Test UserRepositoryCustom.listUserBySharedFile - empty result.")
	public void testListUserBySharedFileEmptyResult() {
		final Long fileId = 1L;
		List<User> users = userRepository.listUserBySharedFile(fileId);
		assertNotNull(users);
		assertTrue(users.isEmpty());
	}
	
	private void checkItem(User user, Long id) {
		assertNotNull(user);
		assertEquals(id, user.getId());
		assertEquals(String.format(USERNAME_FORMAT, id), user.getUsername());
		assertEquals(String.format(PASS_FORMAT, id), user.getPassword());
	}
}
