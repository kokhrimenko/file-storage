package kn.fs.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kn.fs.dao.FileRepository;
import kn.fs.dao.UserRepository;
import kn.fs.domain.FileItem;
import kn.fs.domain.User;
import kn.fs.domain.projections.SharedFileItem;
import kn.fs.service.StorageService;

@Service
public class StorageServiceImpl implements StorageService {

	private static final String FILENAME_PATTERN = "%s/%s";

	@Value("${file_store.path.root}")
	private String storePath;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	@PostConstruct
	public void init() {
		if (storePath == null || storePath.isEmpty()) {
			throw new IllegalArgumentException("Please provide STORE_PATH for correctly initialize StoreService!");
		}

		File storePathRoot = new File(storePath);
		if (!storePathRoot.exists()) {
			throw new IllegalArgumentException("Provided path: " + storePath + " doesn't exists!");
		}

		if (!storePathRoot.isDirectory()) {
			throw new IllegalArgumentException("Provided path: " + storePath + " isn't a directory!");
		}
	}

	private String getFullPathForStore(Long userId) {
		return String.format(FILENAME_PATTERN, storePath, userId);
	}

	protected User getOwner(Long currentUserId) {
		return userRepository.findById(currentUserId).orElseThrow(
				() -> new IllegalArgumentException("Can't load current user details from DB. Please check!"));
	}

	@Override
	@Transactional
	public FileItem store(MultipartFile multipart, Long userId) {
		try {
			String fileName = UUID.randomUUID().toString(), fullFilePath = getFullPathForStore(userId),
					fullFileName = fullFilePath + "/" + fileName;

			File folder = new File(fullFilePath);
			if (!folder.exists() && !new File(fullFilePath).mkdirs()) {
				throw new IllegalArgumentException("Can't create folder for storing user files. Path: " + fullFilePath);
			}

			FileItem fileItem = new FileItem();
			File convFile = new File(fullFileName);

			multipart.transferTo(convFile);
			fileItem.setName(multipart.getOriginalFilename());
			fileItem.setFsPath(fullFileName);
			fileItem.setOwner(getOwner(userId));
			return fileRepository.save(fileItem);
		} catch (IllegalStateException | IOException e) {
			throw new RuntimeException("Can't store file", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<SharedFileItem> loadAll(Long userId) {
		return Stream.concat(fileRepository.findAllByOwner(userId).stream(),
				fileRepository.findAllFromShared(userId).stream()).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public FileItem load(Long fileId, Long userId) {
		return fileRepository.findByIdAndOwner(userId, fileId).orElseThrow(
				() -> new AccessDeniedException("You haven't access to this file. Please try another one"));
	}
}
