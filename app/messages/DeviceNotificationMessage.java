package messages;

import java.io.Serializable;
import java.util.List;

import models.Device;

public class DeviceNotificationMessage implements Serializable {

	private static final long serialVersionUID = -6809160878636364525L;

	private String message;
	private List<Device> devices;

	public DeviceNotificationMessage(String message, List<Device> devices) {
		super();
		this.message = message;
		this.devices = devices;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	@Override
	public String toString() {
		return "DevieNotificationMessage [message=" + message + ", devices=" + devices + "]";
	}

}
