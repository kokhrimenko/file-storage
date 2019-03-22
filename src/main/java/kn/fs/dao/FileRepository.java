package kn.fs.dao;

import org.springframework.data.repository.CrudRepository;

import kn.fs.dao.impl.FileRepositoryCustom;
import kn.fs.domain.FileItem;

public interface FileRepository extends CrudRepository<FileItem, Long>, FileRepositoryCustom {

}
