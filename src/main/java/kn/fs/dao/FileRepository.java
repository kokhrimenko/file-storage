package kn.fs.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import kn.fs.domain.FileItem;
import kn.fs.domain.User;

public interface FileRepository extends CrudRepository<FileItem, Long>{

	List<FileItem> findAllByOwner(User user);
	
}
