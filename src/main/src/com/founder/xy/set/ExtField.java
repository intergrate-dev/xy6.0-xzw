package com.founder.xy.set;

public class ExtField implements Cloneable {
	private Long sys_documentid;
	private Integer sys_doclibid;
	private Long ext_siteID;
	private Long ext_groupID;
	private String ext_name;
	private String ext_code;
	private Long ext_editType;
	private String ext_options;
	private String ext_defaultValue;
	private Integer ext_order;

	private String ext_value;

	public Long getSys_documentid() {
		return sys_documentid;
	}

	public void setSys_documentid(Long sys_documentid) {
		this.sys_documentid = sys_documentid;
	}

	public Integer getSys_doclibid() {
		return sys_doclibid;
	}

	public void setSys_doclibid(Integer sys_doclibid) {
		this.sys_doclibid = sys_doclibid;
	}

	public Long getExt_siteID() {
		return ext_siteID;
	}

	public void setExt_siteID(Long ext_siteID) {
		this.ext_siteID = ext_siteID;
	}

	public Long getExt_groupID() {
		return ext_groupID;
	}

	public void setExt_groupID(Long ext_groupID) {
		this.ext_groupID = ext_groupID;
	}

	public String getExt_name() {
		return ext_name;
	}

	public void setExt_name(String ext_name) {
		this.ext_name = ext_name;
	}

	public String getExt_code() {
		return ext_code;
	}

	public void setExt_code(String ext_code) {
		this.ext_code = ext_code;
	}

	public Long getExt_editType() {
		return ext_editType;
	}

	public void setExt_editType(Long ext_editType) {
		this.ext_editType = ext_editType;
	}

	public String getExt_options() {
		return ext_options;
	}

	public void setExt_options(String ext_options) {
		this.ext_options = ext_options;
	}

	public String getExt_value() {
		return ext_value;
	}

	public void setExt_value(String ext_value) {
		this.ext_value = ext_value;
	}

	public String getExt_defaultValue() {
		return ext_defaultValue;
	}

	public void setExt_defaultValue(String ext_defaultValue) {
		this.ext_defaultValue = ext_defaultValue;
	}

	public Integer getExt_order() {
		return ext_order;
	}

	public void setExt_order(Integer ext_order) {
		this.ext_order = ext_order;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ExtField extField = (ExtField) o;

		if (sys_documentid != null ? !sys_documentid.equals(extField.sys_documentid) : extField.sys_documentid != null)
			return false;
		if (sys_doclibid != null ? !sys_doclibid.equals(extField.sys_doclibid) : extField.sys_doclibid != null)
			return false;
		if (ext_siteID != null ? !ext_siteID.equals(extField.ext_siteID) : extField.ext_siteID != null) return false;
		if (ext_groupID != null ? !ext_groupID.equals(extField.ext_groupID) : extField.ext_groupID != null)
			return false;
		if (ext_name != null ? !ext_name.equals(extField.ext_name) : extField.ext_name != null) return false;
		if (ext_code != null ? !ext_code.equals(extField.ext_code) : extField.ext_code != null) return false;
		if (ext_editType != null ? !ext_editType.equals(extField.ext_editType) : extField.ext_editType != null)
			return false;
		if (ext_options != null ? !ext_options.equals(extField.ext_options) : extField.ext_options != null)
			return false;
		if (ext_defaultValue != null ? !ext_defaultValue.equals(
				extField.ext_defaultValue) : extField.ext_defaultValue != null) return false;
		if (ext_order != null ? !ext_order.equals(extField.ext_order) : extField.ext_order != null) return false;
		return !(ext_value != null ? !ext_value.equals(extField.ext_value) : extField.ext_value != null);

	}

	@Override
	public int hashCode() {
		int result = sys_documentid != null ? sys_documentid.hashCode() : 0;
		result = 31 * result + (sys_doclibid != null ? sys_doclibid.hashCode() : 0);
		result = 31 * result + (ext_siteID != null ? ext_siteID.hashCode() : 0);
		result = 31 * result + (ext_groupID != null ? ext_groupID.hashCode() : 0);
		result = 31 * result + (ext_name != null ? ext_name.hashCode() : 0);
		result = 31 * result + (ext_code != null ? ext_code.hashCode() : 0);
		result = 31 * result + (ext_editType != null ? ext_editType.hashCode() : 0);
		result = 31 * result + (ext_options != null ? ext_options.hashCode() : 0);
		result = 31 * result + (ext_defaultValue != null ? ext_defaultValue.hashCode() : 0);
		result = 31 * result + (ext_order != null ? ext_order.hashCode() : 0);
		result = 31 * result + (ext_value != null ? ext_value.hashCode() : 0);
		return result;
	}

	/*
         * (non-Javadoc)
         *
         * @see java.lang.Object#toString()
         */
	@Override
	public String toString() {
		return "ExtField [sys_documentid=" + sys_documentid + ", sys_doclibid=" + sys_doclibid
				+ ", ext_siteID=" + ext_siteID + ", ext_groupID=" + ext_groupID + ", ext_name="
				+ ext_name + ", ext_code=" + ext_code + ", ext_editType=" + ext_editType
				+ ", ext_options=" + ext_options + ", ext_value=" + ext_value + "]";
	}

}
