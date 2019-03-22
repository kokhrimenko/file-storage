package ks.fs.mvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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
import kn.fs.MvcConfig;
import kn.fs.SecurityConfig;

/**
 * Tests security features.
 * @author kostic
 *
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(classes = { FileStorageApplication.class, SecurityConfig.class, MvcConfig.class })
@DisplayName("Security tests!")
public class SecurityTests {
	private static final String GET_ALL_FILES_URL = "/api/v1/fileStorage/all";
	private static final String LOGIN_URL = "http://localhost/login";
	@Autowired
	private MockMvc mvc;

	@Test
	@DisplayName("Test request secured resource without authorization.")
	void testGetSecuredResourceWithoutAuthoriation() throws Exception {
		this.mvc.perform(get(GET_ALL_FILES_URL)).andExpect(status().isFound())
								.andExpect(redirectedUrl(LOGIN_URL));
	}

	@Test
	@DisplayName("Test request unsecured resource without authorization.")
	void testUnsecuredResourceWithoutAuthorization() throws Exception {
		this.mvc.perform(get("/login")).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Test request secured profile page with mock user.")
	void testProfile() throws Exception {
		this.mvc.perform(get("/profile")).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Test request logout page with mock user.")
	void testLogout() throws Exception {
		this.mvc.perform(get("/logout")).andExpect(status().isFound())
				.andExpect(redirectedUrl("/login?logout"));
	}
	
	@Test
	@WithMockUser
	@DisplayName("Test request upload page with mock user.")
	void testUpload() throws Exception {
		this.mvc.perform(get("/upload")).andExpect(status().isOk());
	}
}
