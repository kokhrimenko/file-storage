package kn.fs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kn.fs.domain.User;

@Service
public interface UserService {

	void share(Long userId, Long fileId, boolean share);
	
	List<User> loadAllExceptCurrent(Long userId);
}
