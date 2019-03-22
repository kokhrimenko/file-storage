package ks.fs.mvc;

import static kn.fs.service.test.UserDetailsServiceImplTest.TEST_USER_ID;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import kn.fs.FileStorageApplication;
import kn.fs.SecurityConfig;
import kn.fs.domain.User;
import kn.fs.service.UserService;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(classes = { FileStorageApplication.class, SecurityConfig.class })
@DisplayName("UserController REST tests!")
@ActiveProfiles({ "test" })
public class UserControllerTests {
	private static final String SHARE_URL = "/api/v1/userManagement/shareFile/" + TEST_USER_ID + "/1";
	private static final String REMOVE_SHARE_URL = "/api/v1/userManagement/removeShareFile/" + TEST_USER_ID + "/1";
	private static final String GET_ALL_URL = "/api/v1/userManagement/all";
	
	private static final String USER_DETAILS_TEST_BEAN_NAME = "TestUserDetailsService";
	
	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserService userService;

	@Test
	@WithMockUser
	@DisplayName("Test UserController.share API call.")
	void testShare() throws Exception {
		this.mvc.perform(post(SHARE_URL)).andExpect(status().isOk()).andExpect(content().string(""));
	}
	
	@Test
	@WithMockUser
	@DisplayName("Test UserController.removeShare API call.")
	void testRemoveShare() throws Exception {
		this.mvc.perform(post(REMOVE_SHARE_URL)).andExpect(status().isOk()).andExpect(content().string(""));
	}
	
	@Test
	@WithUserDetails(value="customUsername", userDetailsServiceBeanName = UserControllerTests.USER_DETAILS_TEST_BEAN_NAME)
	@DisplayName("Test UserController.getAll with null list as a result API call.")
	void testGetAllWithNullResult() throws Exception {
		when(userService.loadAllExceptCurrent(TEST_USER_ID)).thenReturn(null);

		this.mvc.perform(get(GET_ALL_URL)).andExpect(status().isOk()).andExpect(content().string("[]"));
	}

	@Test
	@WithUserDetails(value="customUsername", userDetailsServiceBeanName = UserControllerTests.USER_DETAILS_TEST_BEAN_NAME)
	@DisplayName("Test UserController.getAll with empty list as a result API call.")
	void testGetAllWithEmptyResult() throws Exception {
		when(userService.loadAllExceptCurrent(TEST_USER_ID)).thenReturn(Collections.emptyList());

		this.mvc.perform(get(GET_ALL_URL)).andExpect(status().isOk()).andExpect(content().string("[]"));
	}
	
	@Test
	@WithUserDetails(value="customUsername", userDetailsServiceBeanName = UserControllerTests.USER_DETAILS_TEST_BEAN_NAME)
	@DisplayName("Test UserController.getAll positive case API call.")
	void testGetAll() throws Exception {
		final String usernamePrefix = "usernamePrefix_%s";
		int countOfUsers = 5;
		List<User> users = LongStream.range(0, countOfUsers)
				.mapToObj(id -> new User(id, String.format(usernamePrefix, id)))
				.collect(Collectors.toList());
		when(userService.loadAllExceptCurrent(TEST_USER_ID)).thenReturn(users);

		this.mvc.perform(get(GET_ALL_URL)).andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id", equalTo(0)))
			.andExpect(jsonPath("$[0].name", equalTo(String.format(usernamePrefix, 0))))
			.andExpect(jsonPath("$[0].password").doesNotExist())
			.andExpect(jsonPath("$[1].id", equalTo(1)))
			.andExpect(jsonPath("$[1].name", equalTo(String.format(usernamePrefix, 1))))
			.andExpect(jsonPath("$[1].password").doesNotExist())
			.andExpect(jsonPath("$[2].id", equalTo(2)))
			.andExpect(jsonPath("$[2].name", equalTo(String.format(usernamePrefix, 2))))
			.andExpect(jsonPath("$[2].password").doesNotExist())
			.andExpect(jsonPath("$[3].id", equalTo(3)))
			.andExpect(jsonPath("$[3].name", equalTo(String.format(usernamePrefix, 3))))
			.andExpect(jsonPath("$[3].password").doesNotExist())
			.andExpect(jsonPath("$[4].id", equalTo(4)))
			.andExpect(jsonPath("$[4].name", equalTo(String.format(usernamePrefix, 4))))
			.andExpect(jsonPath("$[4].password").doesNotExist());
	}
}
