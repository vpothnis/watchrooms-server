package messages;

import java.io.Serializable;

public class StatusChangeMessage implements Serializable {

	private static final long serialVersionUID = 335088395677342455L;

	private String roomId;
	private String status;

	public StatusChangeMessage(String roomId, String status) {
		this.roomId = roomId;
		this.status = status;
	}

	public StatusChangeMessage() {

	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "StatusChangeMessage [roomId=" + roomId + ", status=" + status + "]";
	}

}
