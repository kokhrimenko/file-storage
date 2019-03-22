package kn.fs.mvc;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kn.fs.domain.FileItem;
import kn.fs.domain.projections.SharedFileItem;
import kn.fs.mvc.dto.FileItemDTO;
import kn.fs.service.StorageService;
import kn.fs.utils.SecurityUtils;

@RestController
@RequestMapping("/api/v1/fileStorage")
public class FileStorageController {
	private static final String PROFILE_URL = "/profile";

	@Autowired
	private StorageService storageService;

	@GetMapping("/all")
	public List<FileItemDTO> getAll(Principal principal) {
		List<SharedFileItem> files = storageService.loadAll(SecurityUtils.getLoggedUserId(principal));
		if (files == null || files.isEmpty()) {
			return Collections.emptyList();
		}

		return files.stream().map(file -> new FileItemDTO(file.getFileItem(), file.isShared()))
				.collect(Collectors.toList());
	}

	@PostMapping("/uploadFile")
	public void uploadFile(@RequestParam("uploadFile") MultipartFile file, HttpServletResponse response,
			Principal principal) throws IOException {
		storageService.store(file, SecurityUtils.getLoggedUserId(principal));
		response.sendRedirect(PROFILE_URL);
	}

	@GetMapping("/downloadFile/{fileId}")
	public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId, HttpServletRequest request,
			HttpServletResponse respone, Principal principal) throws IOException {
		FileItem fileItem = null;
		try {
			fileItem = storageService.load(fileId, SecurityUtils.getLoggedUserId(principal));
		} catch (AccessDeniedException err) {
			respone.sendError(HttpStatus.FORBIDDEN.value());
			return null;
		} catch (IllegalArgumentException e) {
			respone.sendError(HttpStatus.NOT_FOUND.value());
			return null;
		}

		String contentType = request.getServletContext().getMimeType(fileItem.getName());

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		Path filePath = Paths.get(fileItem.getFsPath());
		Resource resource = new UrlResource(filePath.toUri());
		if (!resource.exists()) {
			respone.sendError(HttpStatus.NOT_FOUND.value());
			return null;
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileItem.getName() + "\"")
				.body(new UrlResource(filePath.toUri()));
	}
}