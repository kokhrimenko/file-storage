package kn.fs.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kn.fs.domain.User;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
	@Autowired
	private EntityManager entityManager;

	@Override
	public List<User> findAllExceptOf(Long userId) {
		TypedQuery<User> query = entityManager.createQuery("from User u WHERE u.id != :id", User.class);
		query.setParameter("id", userId);
		return query.getResultList();
	}

	@Override
	public List<User> listUserBySharedFile(Long fileId) {
		TypedQuery<User> query = entityManager.createQuery(
				"from User u WHERE u.id IN (SELECT usf.user.id FROM UserShareFile usf WHERE usf.fileItem.id=:id)",
				User.class);		
		query.setParameter("id", fileId);
		return query.getResultList();
	}
}
