package kn.fs.dao;

import org.springframework.data.repository.Repository;

import kn.fs.domain.UserShareFile;
import kn.fs.domain.pk.UserShareFileId;

public interface UserShareRepository extends Repository<UserShareFile, UserShareFileId> {
	UserShareFile save(UserShareFile entity);

	void deleteById(UserShareFileId id);

	void delete(UserShareFile entity);
}
