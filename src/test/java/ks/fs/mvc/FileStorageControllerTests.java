package ks.fs.mvc;

import static kn.fs.service.test.UserDetailsServiceImplTest.TEST_USER_ID;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import kn.fs.FileStorageApplication;
import kn.fs.SecurityConfig;
import kn.fs.domain.FileItem;
import kn.fs.domain.projections.SharedFileItem;
import kn.fs.service.StorageService;

/**
 * Tests for FileStorageController REST Controller.
 * @author kostic
 *
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(classes = { FileStorageApplication.class, SecurityConfig.class })
@DisplayName("FileStorageController REST tests!")
@ActiveProfiles({"test"})
public class FileStorageControllerTests {
	private static final String PROFILE_PAGE_URL = "/profile";
	private static final String GET_ALL_FILES_URL = "/api/v1/fileStorage/all";
	private static final String DOWNLOAD_FILE_URL = "/api/v1/fileStorage/downloadFile/%s";
	private static final String UPLOAD_FILE_URL = "/api/v1/fileStorage/uploadFile";
	
	private static final String USER_DETAILS_TEST_BEAN_NAME = "TestUserDetailsService";

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private StorageService storageService;	

	@Test
	@WithUserDetails(value="customUsername", userDetailsServiceBeanName = FileStorageControllerTests.USER_DETAILS_TEST_BEAN_NAME)
	@DisplayName("Test FileStorageController.loadAll with null data returned from the service.")
	void testGetAllWithNullData() throws Exception {
		when(this.storageService.loadAll(TEST_USER_ID)).thenReturn(null);
		this.mvc.perform(get(GET_ALL_FILES_URL)).andExpect(status().isOk())
			.andExpect(content().string("[]"));
	}
	
	@Test
	@WithUserDetails(userDetailsServiceBeanName = FileStorageControllerTests.USER_DETAILS_TEST_BEAN_NAME)
	@DisplayName("Test FileStorageController.loadAll with empty array data returned from the service.")
	void testGetAllWithEmptyData() throws Exception {
		when(this.storageService.loadAll(TEST_USER_ID)).thenReturn(Collections.emptyList());
		this.mvc.perform(get(GET_ALL_FILES_URL)).andExpect(status().isOk())
			.andExpect(content().string("[]"));
	}
	
	@Test
	@WithUserDetails(userDetailsServiceBeanName = FileStorageControllerTests.USER_DETAILS_TEST_BEAN_NAME)
	@DisplayName("Test FileStorageController.loadAll - success case.")
	void testGetAllWithData() throws Exception {
		int countofFiles = 3;
		List<SharedFileItem> results = new ArrayList<>(countofFiles);
		for(int i=0; i< countofFiles; i++) {
			FileItem fileItem = new FileItem();
			fileItem.setId(new Long(i));
			results.add(new SharedFileItem(fileItem, true));
		}
		when(this.storageService.loadAll(TEST_USER_ID)).thenReturn(results);
		this.mvc.perform(get(GET_ALL_FILES_URL)).andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id", equalTo(0)))
			.andExpect(jsonPath("$[1].id", equalTo(1)))
			.andExpect(jsonPath("$[2].id", equalTo(2)));
	}
	
	@Test
	@WithUserDetails(userDetailsServiceBeanName = FileStorageControllerTests.USER_DETAILS_TEST_BEAN_NAME)
	@DisplayName("Test FileStorageController.downloadFile without required privileges.")
	void testDownloadFileWithAccessDeny() throws Exception {
		doThrow(AccessDeniedException.class).when(storageService).load(anyLong(), anyLong());
		
		this.mvc.perform(get(String.format(DOWNLOAD_FILE_URL, 1))).andExpect(status().isForbidden());
	}
	
	@Test
	@WithUserDetails(userDetailsServiceBeanName = FileStorageControllerTests.USER_DETAILS_TEST_BEAN_NAME)
	@DisplayName("Test FileStorageController.downloadFile with wrong fileId.")
	void testDownloadFileWithIllegalArgumentException() throws Exception {
		doThrow(IllegalArgumentException.class).when(storageService).load(anyLong(), anyLong());
		
		this.mvc.perform(get(String.format(DOWNLOAD_FILE_URL, 1))).andExpect(status().isNotFound());
	}
	
	@Test
	@WithUserDetails(userDetailsServiceBeanName = FileStorageControllerTests.USER_DETAILS_TEST_BEAN_NAME)
	@DisplayName("Test FileStorageController.downloadFile when file doesn't exists on FS.")
	void testDownloadFileWithoutFileOnFS() throws Exception {
		FileItem fileItem = new FileItem();
		fileItem.setFsPath("/some_rundom_value");
		when(this.storageService.load(anyLong(), anyLong())).thenReturn(fileItem);
		
		this.mvc.perform(get(String.format(DOWNLOAD_FILE_URL, 1))).andExpect(status().isNotFound());
	}
	
	@Test
	@WithUserDetails(userDetailsServiceBeanName = FileStorageControllerTests.USER_DETAILS_TEST_BEAN_NAME)
	@DisplayName("Test FileStorageController.downloadFile - success case.")
	void testDownloadFile() throws Exception {
		FileItem fileItem = new FileItem();
		fileItem.setFsPath("/");
		fileItem.setName("tmp.html");
		when(this.storageService.load(anyLong(), anyLong())).thenReturn(fileItem);
		
		this.mvc.perform(get(String.format(DOWNLOAD_FILE_URL, 1))).andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileItem.getName() + "\""))
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE));
	}
	
	@Test
	@WithUserDetails(userDetailsServiceBeanName = FileStorageControllerTests.USER_DETAILS_TEST_BEAN_NAME)
	@DisplayName("Test FileStorageController.uploadFile - success case.")
	void testUploadFile() throws Exception {
		MockMultipartFile uploadedFile = new MockMultipartFile("uploadFile", "uploadFile.txt", "text/plain",
				"unit test".getBytes());

		this.mvc.perform(multipart(UPLOAD_FILE_URL).file(uploadedFile)).andExpect(status().isFound())
				.andExpect(redirectedUrl(PROFILE_PAGE_URL));
	}
}