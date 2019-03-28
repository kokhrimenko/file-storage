package ks.fs.mvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import kn.fs.FileStorageApplication;
import kn.fs.domain.User;
import kn.fs.mvc.dto.UserRegistrationDTO;
import kn.fs.service.UserService;

/**
 * Tests for RegistrationControllerTests Controller.
 * @author kostic
 *
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(classes = { FileStorageApplication.class })
@DisplayName("RegistrationController tests!")
@ActiveProfiles({"test"})
public class RegistrationControllerTests {
	private static final String WRONG_PASSWORD_ERROR_CODE = "wrongPassword";
	private static final String ALREADY_EXISTS_ERROR_CODE = "alreadyExists";
	private static final String WRONG_FIELD_VALUE_LENGTH_ERROR_CODE = "Size";
	private static final String NULL_VALUE_FIELD_ERROR_CODE = "NotNull";
	private static final String MODEL_ATTRIBUTE_NAME = "user";
	private static final String PASSWORD_FIELD_NAME = "password";
	private static final String CONFIRM_PASSWORD_FIELD_NAME = "confirmPassword";
	private static final String USERNAME_FIELD_NAME = "username";
	
	private static final String REGISTRATION_URL = "/register";
	private static final String REGISTRATION_SUCCESS_URL = "/register?success";
	private static final String REGISTRATION_VIEW_NAME = "register";

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private UserService userService;

	@Test
	@DisplayName("Test RegistrationController.registerUserAccount - not equals password's.")
	void testRegisterUserAccountWithDifferentPasswords() throws Exception {
		UserRegistrationDTO userData = new UserRegistrationDTO();
		userData.setPassword("password");
		userData.setConfirmPassword("not the same");
		userData.setUsername("username");
		this.mvc.perform(
				post(REGISTRATION_URL).contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.param(PASSWORD_FIELD_NAME, userData.getPassword())
					.param(CONFIRM_PASSWORD_FIELD_NAME, userData.getConfirmPassword())
					.param(USERNAME_FIELD_NAME, userData.getUsername())
					.sessionAttr(MODEL_ATTRIBUTE_NAME, new UserRegistrationDTO()))
				.andExpect(status().isOk()).andExpect(view().name(REGISTRATION_VIEW_NAME))
				.andExpect(model().attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME, PASSWORD_FIELD_NAME))
				.andExpect(model().attributeHasFieldErrorCode(MODEL_ATTRIBUTE_NAME, PASSWORD_FIELD_NAME, WRONG_PASSWORD_ERROR_CODE))
				.andExpect(model().errorCount(1));
	}
	
	@Test
	@DisplayName("Test RegistrationController.registerUserAccount - too short password.")
	void testRegisterUserAccountWithShortPasswords() throws Exception {
		UserRegistrationDTO userData = new UserRegistrationDTO();
		userData.setPassword("passwo");
		userData.setConfirmPassword("not the same");
		userData.setUsername("username");
		this.mvc.perform(
				post(REGISTRATION_URL).contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.param(PASSWORD_FIELD_NAME, userData.getPassword())
					.param(CONFIRM_PASSWORD_FIELD_NAME, userData.getConfirmPassword())
					.param(USERNAME_FIELD_NAME, userData.getUsername())
					.sessionAttr(MODEL_ATTRIBUTE_NAME, new UserRegistrationDTO()))
				.andExpect(status().isOk()).andExpect(view().name(REGISTRATION_VIEW_NAME))
				.andExpect(model().attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME, PASSWORD_FIELD_NAME))
				.andExpect(model().attributeHasFieldErrorCode(MODEL_ATTRIBUTE_NAME, PASSWORD_FIELD_NAME, WRONG_FIELD_VALUE_LENGTH_ERROR_CODE))
				.andExpect(model().errorCount(1));
	}
	
	@Test
	@DisplayName("Test RegistrationController.registerUserAccount - null password.")
	void testRegisterUserAccountWithNullPasswords() throws Exception {
		UserRegistrationDTO userData = new UserRegistrationDTO();
		userData.setPassword(null);
		userData.setConfirmPassword("not the same");
		userData.setUsername("username");
		this.mvc.perform(
				post(REGISTRATION_URL).contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.param(PASSWORD_FIELD_NAME, userData.getPassword())
					.param(CONFIRM_PASSWORD_FIELD_NAME, userData.getConfirmPassword())
					.param(USERNAME_FIELD_NAME, userData.getUsername())
					.sessionAttr(MODEL_ATTRIBUTE_NAME, new UserRegistrationDTO()))
				.andExpect(status().isOk()).andExpect(view().name(REGISTRATION_VIEW_NAME))
				.andExpect(model().attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME, PASSWORD_FIELD_NAME))
				.andExpect(model().attributeHasFieldErrorCode(MODEL_ATTRIBUTE_NAME, PASSWORD_FIELD_NAME, NULL_VALUE_FIELD_ERROR_CODE))
				.andExpect(model().errorCount(1));
	}
	
	@Test
	@DisplayName("Test RegistrationController.registerUserAccount - too short username.")
	void testRegisterUserAccountWithShortUsername() throws Exception {
		UserRegistrationDTO userData = new UserRegistrationDTO();
		userData.setPassword("password");
		userData.setConfirmPassword("not the same");
		userData.setUsername("");
		this.mvc.perform(
				post(REGISTRATION_URL).contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.param(PASSWORD_FIELD_NAME, userData.getPassword())
					.param(CONFIRM_PASSWORD_FIELD_NAME, userData.getConfirmPassword())
					.param(USERNAME_FIELD_NAME, userData.getUsername())
					.sessionAttr(MODEL_ATTRIBUTE_NAME, new UserRegistrationDTO()))
				.andExpect(status().isOk()).andExpect(view().name(REGISTRATION_VIEW_NAME))
				.andExpect(model().attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME, USERNAME_FIELD_NAME))
				.andExpect(model().attributeHasFieldErrorCode(MODEL_ATTRIBUTE_NAME, USERNAME_FIELD_NAME, WRONG_FIELD_VALUE_LENGTH_ERROR_CODE))
				.andExpect(model().errorCount(1));
	}
	
	@Test
	@DisplayName("Test RegistrationController.registerUserAccount - null username.")
	void testRegisterUserAccountWithNullUsername() throws Exception {
		UserRegistrationDTO userData = new UserRegistrationDTO();
		userData.setPassword("password");
		userData.setConfirmPassword("not the same");
		userData.setUsername(null);
		this.mvc.perform(
				post(REGISTRATION_URL).contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.param(PASSWORD_FIELD_NAME, userData.getPassword())
					.param(CONFIRM_PASSWORD_FIELD_NAME, userData.getConfirmPassword())
					.param(USERNAME_FIELD_NAME, userData.getUsername())
					.sessionAttr(MODEL_ATTRIBUTE_NAME, new UserRegistrationDTO()))
				.andExpect(status().isOk()).andExpect(view().name(REGISTRATION_VIEW_NAME))
				.andExpect(model().attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME, USERNAME_FIELD_NAME))
				.andExpect(model().attributeHasFieldErrorCode(MODEL_ATTRIBUTE_NAME, USERNAME_FIELD_NAME, NULL_VALUE_FIELD_ERROR_CODE))
				.andExpect(model().errorCount(1));
	}
	
	@Test
	@DisplayName("Test RegistrationController.registerUserAccount - busy username.")
	void testRegisterUserAccountWithAlreadyExistsUsername() throws Exception {		
		UserRegistrationDTO userData = new UserRegistrationDTO();
		userData.setPassword("password");
		userData.setConfirmPassword(userData.getPassword());
		userData.setUsername("test user name");
		
		when(userService.loadUserByUsername(userData.getUsername())).thenReturn(new User());
		
		this.mvc.perform(
				post(REGISTRATION_URL).contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.param(PASSWORD_FIELD_NAME, userData.getPassword())
					.param(CONFIRM_PASSWORD_FIELD_NAME, userData.getConfirmPassword())
					.param(USERNAME_FIELD_NAME, userData.getUsername())
					.sessionAttr(MODEL_ATTRIBUTE_NAME, new UserRegistrationDTO()))
				.andExpect(status().isOk()).andExpect(view().name(REGISTRATION_VIEW_NAME))
				.andExpect(model().attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME, USERNAME_FIELD_NAME))
				.andExpect(model().attributeHasFieldErrorCode(MODEL_ATTRIBUTE_NAME, USERNAME_FIELD_NAME, ALREADY_EXISTS_ERROR_CODE))
				.andExpect(model().errorCount(1));
	}
	
	@Test
	@DisplayName("Test RegistrationController.registerUserAccount - success case.")
	void testRegisterUserAccountSuccess() throws Exception {		
		UserRegistrationDTO userData = new UserRegistrationDTO();
		userData.setPassword("password");
		userData.setConfirmPassword(userData.getPassword());
		userData.setUsername("test user name");

		this.mvc.perform(
				post(REGISTRATION_URL).contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.param(PASSWORD_FIELD_NAME, userData.getPassword())
					.param(CONFIRM_PASSWORD_FIELD_NAME, userData.getConfirmPassword())
					.param(USERNAME_FIELD_NAME, userData.getUsername())
					.sessionAttr(MODEL_ATTRIBUTE_NAME, new UserRegistrationDTO()))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl(REGISTRATION_SUCCESS_URL));
		
		verify(userService, times(1)).createUser(userData.toUser());
	}
}