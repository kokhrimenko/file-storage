package ks.fs.mvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import kn.fs.FileStorageApplication;
import kn.fs.domain.FileItem;
import kn.fs.service.StorageService;

/**
 * Tests for FileStorageController REST Controller.
 * @author kostic
 *
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(classes = { FileStorageApplication.class })
public class FileStorageControllerTests {
	private static final String GET_ALL_FILES_URL = "/api/v1/fileStorage/all";
	
	private static final String DOWNLOAD_FILE_URL = "/api/v1/fileStorage/downloadFile/%s";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private StorageService storageService;	

	@Test
	@WithMockUser
	@DisplayName("Test FileStorageController.loadAll with null data returned from the ervice.")
	void testGetAllWithNullData() throws Exception {
		given(this.storageService.loadAll()).willReturn(null);
		this.mvc.perform(get(GET_ALL_FILES_URL)).andExpect(status().isOk())
			.andExpect(content().string("[]"));
	}
	
	@Test
	@WithMockUser
	@DisplayName("Test FileStorageController.loadAll with empty array data returned from the ervice.")
	void testGetAllWithEmptyData() throws Exception {
		given(this.storageService.loadAll()).willReturn(Collections.emptyList());
		this.mvc.perform(get(GET_ALL_FILES_URL)).andExpect(status().isOk())
			.andExpect(content().string("[]"));
	}
	
	@Test
	@WithMockUser
	@DisplayName("Test FileStorageController.loadAll with success response and list of valies.")
	void testGetAllWithData() throws Exception {
		int countofFiles = 3;
		List<FileItem> results = new ArrayList<>(countofFiles);
		for(int i=0; i< countofFiles; i++) {
			FileItem fileItem = new FileItem();
			fileItem.setId(new Long(i));
			results.add(fileItem);
		}
		given(this.storageService.loadAll()).willReturn(results);
		this.mvc.perform(get(GET_ALL_FILES_URL)).andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id", is(equalTo(0))))
			.andExpect(jsonPath("$[1].id", is(equalTo(1))))
			.andExpect(jsonPath("$[2].id", is(equalTo(2))));
	}
	
	@Test
	@WithMockUser
	@DisplayName("Test FileStorageController.downloadFile without required privileges.")
	void testDownloadFileWithAccessDeny() throws Exception {
		given(this.storageService.load(anyLong())).willThrow(AccessDeniedException.class);
		
		this.mvc.perform(get(String.format(DOWNLOAD_FILE_URL, 1))).andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Test FileStorageController.downloadFile with wrong fileId.")
	void testDownloadFileWithIllegalArgumentException() throws Exception {
		given(this.storageService.load(anyLong())).willThrow(IllegalArgumentException.class);
		
		this.mvc.perform(get(String.format(DOWNLOAD_FILE_URL, 1))).andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Test FileStorageController.downloadFile when file doesn't exists on FS.")
	void testDownloadFileWithoutFileOnFS() throws Exception {
		FileItem fileItem = new FileItem();
		fileItem.setFsPath("/some_rundom_value");
		given(this.storageService.load(anyLong())).willReturn(fileItem);
		
		this.mvc.perform(get(String.format(DOWNLOAD_FILE_URL, 1))).andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser
	@DisplayName("Test FileStorageController.downloadFile - success case.")
	void testDownloadFile() throws Exception {
		FileItem fileItem = new FileItem();
		fileItem.setFsPath("/");
		fileItem.setName("tmp.html");
		given(this.storageService.load(anyLong())).willReturn(fileItem);
		
		this.mvc.perform(get(String.format(DOWNLOAD_FILE_URL, 1))).andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileItem.getName() + "\""))
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE));
	}
}
