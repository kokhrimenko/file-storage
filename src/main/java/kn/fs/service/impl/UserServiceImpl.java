package kn.fs.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kn.fs.dao.FileRepository;
import kn.fs.dao.UserRepository;
import kn.fs.dao.UserShareRepository;
import kn.fs.domain.FileItem;
import kn.fs.domain.User;
import kn.fs.domain.UserShareFile;
import kn.fs.domain.pk.UserShareFileId;
import kn.fs.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserShareRepository userShareRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FileRepository fileRepository;

	@Override
	@Transactional
	public void share(Long userId, Long fileId, boolean share) {
		if (!share) {
			userShareRepository.deleteById(new UserShareFileId(userId, fileId));
			return;
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("Couldn't found user with id: " + userId));
		FileItem fileItem = fileRepository.findById(fileId)
				.orElseThrow(() -> new IllegalArgumentException("Couldn't found file with id: " + userId));

		UserShareFile userShare = new UserShareFile(user, fileItem);
		userShare.setCreatedOn(new Date());
		userShareRepository.save(userShare);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<User> loadAllExceptCurrent(Long userId) {
		return userRepository.findAllExcectOf(userId);
	}

}