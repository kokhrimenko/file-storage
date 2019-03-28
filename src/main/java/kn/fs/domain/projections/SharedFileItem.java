package kn.fs.domain.projections;

import kn.fs.domain.FileItem;

public class SharedFileItem {

	private FileItem fileItem;
	private boolean shared = false;

	public SharedFileItem(FileItem fileItem, boolean shared) {
		super();
		this.fileItem = fileItem;
		this.shared = shared;
	}

	public FileItem getFileItem() {
		return fileItem;
	}

	public boolean isShared() {
		return shared;
	}

}
