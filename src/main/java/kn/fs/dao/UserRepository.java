package kn.fs.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import kn.fs.dao.impl.UserRepositoryCustom;
import kn.fs.domain.User;

public interface UserRepository extends CrudRepository<User, Long>, UserRepositoryCustom {
	Optional<User> findByUsername(String username);
}
