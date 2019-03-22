package kn.fs.mvc.dto;

import kn.fs.domain.FileItem;

public class FileItemDTO {

	private Long id;
	private String name;
	private boolean isShared = false;

	public FileItemDTO(FileItem fileItem, boolean isShared) {
		super();
		if(fileItem == null) {
			throw new IllegalAccessError("File can't be NULL!");
		}
	
		this.id = fileItem.getId();
		this.name = fileItem.getName();
		this.isShared = isShared;
	}
	
	public Long getId() {
		return id;
	}
	public String getName() {
		return name;
	}

	public boolean isShared() {
		return isShared;
	}
}
