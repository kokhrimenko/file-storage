package kn.fs.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kn.fs.dao.FileRepository;
import kn.fs.dao.UserRepository;
import kn.fs.domain.FileItem;
import kn.fs.domain.User;
import kn.fs.security.UserDetails;
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
		if(storePath == null || storePath.isEmpty()) {
			throw new IllegalArgumentException("Please provide STORE_PATH for correctly initialize StoreService!");
		}
		
		File storePathRoot = new File(storePath);
		if(!storePathRoot.exists()) {
			throw new IllegalArgumentException("Provided path: " + storePath +" doesn't exists!");
		}
		
		if(!storePathRoot.isDirectory()) {
			throw new IllegalArgumentException("Provided path: " + storePath + " isn't a directory!");
		}
	}

	private UserDetails getCurrentUser() {
		return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	private String getFullPathForStore() {
		Long currentUserId = getCurrentUser().getUserId();
		return String.format(FILENAME_PATTERN, storePath, currentUserId);
	}
	
	private User getOwner(Long currentUserId) {
		User currentUser = userRepository.findById(currentUserId);
		if (currentUser == null) {
			throw new IllegalArgumentException("Can't load current user details from DB. Please check!");
		}
		return currentUser;
	}
	
	@Override
	@Transactional
	public FileItem store(MultipartFile multipart) {
	    try {
	    	String fileName = UUID.randomUUID().toString(),
	    			fullFilePath = getFullPathForStore(),
	    			fullFileName = fullFilePath + "/" + fileName;
	    	
	    	User currentUser = getOwner(getCurrentUser().getUserId());
	    	File folder = new File(fullFilePath);
	    	if(!folder.exists() && !new File(fullFilePath).mkdirs()) {
				throw new IllegalArgumentException("Can't create folder for storing user files. Path: " + fullFilePath);
			}
	    	
	    	FileItem fileItem = new FileItem();
			File convFile = new File(fullFileName);
			
			multipart.transferTo(convFile);
			fileItem.setName(multipart.getOriginalFilename());
			fileItem.setFsPath(fullFileName);
			fileItem.setOwner(currentUser);
			return fileRepository.save(fileItem);
		} catch (IllegalStateException | IOException e) {
			throw new RuntimeException("Can't store file", e);
		}
	}

	@Override
	@Transactional
	public List<FileItem> loadAll() {
		return fileRepository.findAllByOwner(getOwner(getCurrentUser().getUserId()));
	}

	@Override
	public FileItem load(Long fileId) {
		User currentUser = getOwner(getCurrentUser().getUserId());

		Optional<FileItem> fileItem = fileRepository.findById(fileId);
		if (!fileItem.isPresent()) {
			throw new IllegalArgumentException("Can't found requested file. Please check!");
		}

		if (!fileItem.get().getOwner().equals(currentUser)) {
			throw new AccessDeniedException("You haven't access to this file. Please try another one");
		}

		return fileItem.get();
	}
}
