package kn.fs.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import kn.fs.domain.User;

public interface UserRepository extends Repository<User, Long> {
	Optional<User> findByUsername(String username);
	
	Optional<User> findById(Long id);
	
	List<User> findAll();
	
	List<User> findAllExcectOf(Long userId);
}
