package kn.fs.domain.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserShareFileId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3852668333275821995L;

	@Column(name = "USER_ID")
	private Long userId;
	
	@Column(name = "FILE_ID")
	private Long fileId;

	public UserShareFileId() {
		super();
	}

	public UserShareFileId(Long userId, Long fileId) {
		super();
		this.userId = userId;
		this.fileId = fileId;
	}

	public Long getUserId() {
		return userId;
	}

	public Long getFileId() {
		return fileId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileId == null) ? 0 : fileId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserShareFileId other = (UserShareFileId) obj;
		if (fileId == null) {
			if (other.fileId != null)
				return false;
		} else if (!fileId.equals(other.fileId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserShareFileId [userId=" + userId + ", fileId=" + fileId + "]";
	}
}
