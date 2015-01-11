package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BaseModel {

	@JsonProperty
	private String name;

	@JsonProperty
	private Long createDate;

	@JsonProperty
	private Long updateDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

	public Long getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Long updateDate) {
		this.updateDate = updateDate;
	}

}
