package kn.fs.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import kn.fs.dao.UserRepository;
import kn.fs.domain.User;

@Repository
public class UserRepositoryImpl extends SimpleJpaRepository<User, Long> implements UserRepository {

	private EntityManager entityManager;

	@Autowired
	public UserRepositoryImpl(EntityManager entityManager) {
		super(User.class, entityManager);

		this.entityManager = entityManager;
	}

	@Override
	public Optional<User> findByUsername(String username) {
		TypedQuery<User> query = entityManager.createQuery("from User WHERE username= :username", User.class);
		query.setParameter("username", username);
		return query.getResultList().stream().findFirst();
	}

	@Override
	public List<User> findAllExcectOf(Long userId) {
		TypedQuery<User> query = entityManager.createQuery("from User u WHERE u.id != :id", User.class);
		query.setParameter("id", userId);
		return query.getResultList();
	}

}
