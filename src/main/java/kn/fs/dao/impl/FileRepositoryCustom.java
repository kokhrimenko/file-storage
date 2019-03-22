package kn.fs.dao.impl;

import java.util.List;
import java.util.Optional;

import kn.fs.domain.FileItem;
import kn.fs.domain.projections.SharedFileItem;

public interface FileRepositoryCustom {
	List<SharedFileItem> findAllFromShared(Long userId);
	
	List<SharedFileItem> findAllByOwner(Long userId);
	
	Optional<FileItem> findByIdAndOwner(Long userId, Long fileId);
}
