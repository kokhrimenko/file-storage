package kn.fs.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;

import kn.fs.domain.FileItem;
import kn.fs.domain.projections.SharedFileItem;

public class FileRepositoryCustomImpl implements FileRepositoryCustom {

	@Autowired
	private EntityManager entityManager;
	
	@Override
	public List<SharedFileItem> findAllFromShared(Long userId) {
		TypedQuery<SharedFileItem> query = entityManager.createQuery(
				"SELECT new  kn.fs.domain.projections.SharedFileItem(fi,true) "
				+ "from FileItem fi "
				+ "where fi.id in (SELECT usf.fileItem.id from UserShareFile usf WHERE usf.user.id = :userId) "
				+ "ORDER BY fi.id ASC",
				SharedFileItem.class);
		query.setParameter("userId", userId);

		return query.getResultList();
	}

	@Override
	public List<SharedFileItem> findAllByOwner(Long userId) {
		TypedQuery<SharedFileItem> query = entityManager.createQuery(
				"SELECT new  kn.fs.domain.projections.SharedFileItem(fi,false) "
				+ "from FileItem fi "
				+ "where fi.owner.id = :userId "
				+ "ORDER BY fi.id ASC",
				SharedFileItem.class);
		query.setParameter("userId", userId);

		return query.getResultList();
	}

	@Override
	public Optional<FileItem> findByIdAndOwner(Long userId, Long fileId) {
		TypedQuery<FileItem> query = entityManager.createQuery(
				"from FileItem fi WHERE (fi.owner.id =:userId AND fi.id = :fileId) or true = (SELECT true from UserShareFile usf WHERE usf.user.id = :userId AND usf.fileItem.id = :fileId)",
				FileItem.class);
		query.setParameter("userId", userId);
		query.setParameter("fileId", fileId);
		return query.getResultList().stream().findFirst();
	}
}