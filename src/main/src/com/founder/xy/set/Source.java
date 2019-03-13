package com.founder.xy.set;

public class Source {
	private Long id;
	private Integer libID;
	private Long siteID;
	private Long groupID;
	private String name;
	private String url;
	private String description;
	private String icon;

	public Long getId() {
		return id;
	}

	public void setId(Long sys_documentid) {
		this.id = sys_documentid;
	}

	public Integer getLibID() {
		return libID;
	}

	public void setLibID(Integer sys_doclibid) {
		this.libID = sys_doclibid;
	}

	public Long getSiteID() {
		return siteID;
	}

	public void setSiteID(Long src_siteID) {
		this.siteID = src_siteID;
	}

	public Long getGroupID() {
		return groupID;
	}

	public void setGroupID(Long src_groupID) {
		this.groupID = src_groupID;
	}

	public String getName() {
		return name;
	}

	public void setName(String src_name) {
		this.name = src_name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String src_url) {
		this.url = src_url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String src_description) {
		this.description = src_description;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String src_icon) {
		this.icon = src_icon;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((groupID == null) ? 0 : groupID.hashCode());
		result = prime * result + ((icon == null) ? 0 : icon.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((siteID == null) ? 0 : siteID.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((libID == null) ? 0 : libID.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Source other = (Source) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (groupID == null) {
			if (other.groupID != null)
				return false;
		} else if (!groupID.equals(other.groupID))
			return false;
		if (icon == null) {
			if (other.icon != null)
				return false;
		} else if (!icon.equals(other.icon))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (siteID == null) {
			if (other.siteID != null)
				return false;
		} else if (!siteID.equals(other.siteID))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (libID == null) {
			if (other.libID != null)
				return false;
		} else if (!libID.equals(other.libID))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Source [id=" + id + ", libID=" + libID
				+ ", siteID=" + siteID + ", groupID=" + groupID
				+ ", name=" + name + ", url=" + url
				+ ", description=" + description + ", icon=" + icon
				+ "]";
	}
}
