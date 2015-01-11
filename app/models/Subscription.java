package models;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Document(collection = "subscriptions")
@CompoundIndexes({ @CompoundIndex(name = "roomId_UserId_unique_index", def = "{'roomId': 1, 'userId': 1}", unique = true) })
@JsonSerialize
public class Subscription {

	@Indexed
	@JsonProperty
	private String roomId;

	@Indexed
	@JsonProperty
	private String userId;

	public Subscription() {
	}

	public Subscription(String roomId, String userId) {
		super();
		this.roomId = roomId;
		this.userId = userId;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String floorId) {
		this.userId = floorId;
	}

	@Override
	public String toString() {
		return "Subscription [roomId=" + roomId + ", userId=" + userId + "]";
	}

}
