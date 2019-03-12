package kn.fs.mvc.dto;

import kn.fs.domain.FileItem;

public class FileItemDTO {

	private Long id;
	private String name;
		
	public FileItemDTO(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public FileItemDTO(FileItem fileItem) {
		super();
		if(fileItem == null) {
			throw new IllegalAccessError("File can't be NULL!");
		}
	
		this.id = fileItem.getId();
		this.name = fileItem.getName();
	}
	
	public Long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	
	
}
