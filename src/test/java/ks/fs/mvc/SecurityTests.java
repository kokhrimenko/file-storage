package ks.fs.mvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import kn.fs.FileStorageApplication;

/**
 * Tests security features.
 * @author kostic
 *
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(classes = { FileStorageApplication.class })
public class SecurityTests {
	private static final String GET_ALL_FILES_URL = "/api/v1/fileStorage/all";
	@Autowired
	private MockMvc mvc;

	@Test
	@DisplayName("Test Requests secured resource without authorization.")
	void testGetSecuredResourceWithoutAuthoriation() throws Exception {
		this.mvc.perform(get(GET_ALL_FILES_URL)).andExpect(status().isUnauthorized());
	}
	
	@Test
	@DisplayName("Test Requests unsecured resource without authorization.")
	void testUnsecuredResourceWithoutAuthorization() throws Exception {
		this.mvc.perform(get("/login")).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Test Requests secured profile page with mock user.")
	void testProfile() throws Exception {
		this.mvc.perform(get("/login")).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Test Requests logout page with mock user.")
	void testLogout() throws Exception {
		this.mvc.perform(get("/logout")).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Test Requests upload page with mock user.")
	void testUpload() throws Exception {
		this.mvc.perform(get("/upload")).andExpect(status().isOk());
	}
}
