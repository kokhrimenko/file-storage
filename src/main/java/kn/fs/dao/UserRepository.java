package kn.fs.dao;

import org.springframework.data.repository.Repository;

import kn.fs.domain.User;

public interface UserRepository extends Repository<User, Long> {
	User findByUsername(String username);
	
	User findById(Long id);
}
