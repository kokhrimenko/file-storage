package kn.fs.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import kn.fs.domain.pk.UserShareFileId;

@Entity
@Table(name = "USER_SHARE")
public class UserShareFile {
	@EmbeddedId
	private UserShareFileId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("userId")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("fileId")
	private FileItem fileItem;

	@Column(name = "created_on")
	private Date createdOn = new Date();

	public UserShareFile() {
		super();
	}

	public UserShareFile(User user, FileItem fileItem) {
		super();
		this.id = new UserShareFileId(user.getId(), fileItem.getId());
		this.user = user;
		this.fileItem = fileItem;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public FileItem getFileItem() {
		return fileItem;
	}

	public void setFileItem(FileItem fileItem) {
		this.fileItem = fileItem;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((fileItem == null) ? 0 : fileItem.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		UserShareFile other = (UserShareFile) obj;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
			return false;
		if (fileItem == null) {
			if (other.fileItem != null)
				return false;
		} else if (!fileItem.equals(other.fileItem))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserShareFile [id=" + id + ", user=" + user + ", fileItem=" + fileItem + ", createdOn=" + createdOn
				+ "]";
	}

}
