package models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Document(collection = "rooms")
@JsonSerialize
public class Room extends BaseModel {

	@Id
	@JsonProperty
	private String roomId;

	@Indexed
	@JsonProperty
	private String floorId;

	@JsonProperty
	private String buildingId;

	@JsonProperty
	private String gender;

	@JsonProperty
	private RoomStatus currentStatus;
	
	@JsonProperty
	private boolean subscribed;

	public enum RoomStatus {
		OCCUPIED, AVAILABLE, CLEANING_IN_PROGRESS
	}

	public Room() {
	}

	public Room(String roomId, String floorId, String buildingId, String gender, RoomStatus currentStatus) {
		super();
		this.roomId = roomId;
		this.floorId = floorId;
		this.buildingId = buildingId;
		this.gender = gender;
		this.currentStatus = currentStatus;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public RoomStatus getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(RoomStatus currentStatus) {
		this.currentStatus = currentStatus;
	}
	
	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}

	@Override
	public String toString() {
		return "Room [roomId=" + roomId + ", floorId=" + floorId + ", buildingId=" + buildingId + ", gender=" + gender + ", currentStatus=" + currentStatus + ", getName()=" + getName()
		        + ", getCreateDate()=" + getCreateDate() + ", getUpdateDate()=" + getUpdateDate() + "]";
	}

}
