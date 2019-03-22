package kn.fs.dao.impl;

import java.util.List;

import kn.fs.domain.User;

public interface UserRepositoryCustom {
	List<User> findAllExceptOf(Long userId);
	
	List<User> listUserBySharedFile(Long fileId);
}
