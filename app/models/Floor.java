package models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Document(collection = "floors")
@JsonSerialize
public class Floor extends BaseModel {

	@Id
	@JsonProperty
	private String floorId;

	@JsonProperty
	private String buildingId;

	public Floor(String floorId, String buildingId, String name) {
		super();
		this.floorId = floorId;
		this.buildingId = buildingId;
		super.setName(name);
	}

	public Floor() {

	}

	public String getFloorId() {
		return floorId;
	}

	public void setFloorId(String floorId) {
		this.floorId = floorId;
	}

	public String getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}

	@Override
	public String toString() {
		return "Floor [floorId=" + floorId + ", buildingId=" + buildingId + ", name=" + getName() + ", getCreateDate()=" + getCreateDate() + ", getUpdateDate()=" + getUpdateDate() + "]";
	}

}
