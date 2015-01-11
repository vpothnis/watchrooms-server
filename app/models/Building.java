package models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Document(collection = "buildings")
@JsonSerialize
public class Building extends BaseModel {

	@Id
	@JsonProperty
	private String buildingId;

	@JsonProperty
	private String address;

	public Building(String buildingId, String name, String address) {
		super();
		this.buildingId = buildingId;
		super.setName(name);
		this.address = address;
	}

	public Building() {
	}

	public String getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Building [buildingId=" + buildingId + ", name=" + getName() + ", address=" + address + ", createDate=" + getCreateDate() + ", updateDate=" + getUpdateDate() + "]";
	}

}
