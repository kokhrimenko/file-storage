package kn.fs.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kn.fs.domain.FileItem;
import kn.fs.domain.projections.SharedFileItem;

@Service
public interface StorageService {
    void init();

    /**
     * 
     * @param file
     * @return
     * @throws IllegalArgumentException - if we can't create folder for storing file
     * 		RuntimeException - if we can't store file to the FS
     * 			
     */
    FileItem store(MultipartFile file, Long userId);

    List<SharedFileItem> loadAll(Long userId);

    /**
     * 
     * @param fileId
     * @return
     * @throws IllegalArgumentException - if file didn't exists into the DB
     * 			AccessDeniedException - if current logged user doesn't have access to this file
     */
    FileItem load(Long fileId, Long userId);

    //We don't need this methods at the moment related to the TASK!.
    //Resource loadAsResource(String filename);
    //void deleteAll();
}