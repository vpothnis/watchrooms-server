package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class Device extends BaseModel {

	@JsonProperty()
	private String deviceId;

	@JsonProperty
	private DevicePlatform platform;

	public Device() {

	}

	public Device(String deviceId, String name) {
		super();
		this.deviceId = deviceId;
		super.setName(name);
	}

	public DevicePlatform getPlatform() {
		return platform;
	}

	public void setPlatform(DevicePlatform platform) {
		this.platform = platform;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String toString() {
		return "Device [deviceId=" + deviceId + ", platform=" + platform + ", getName()=" + getName() + "]";
	}

	public enum DevicePlatform {
		IOS, ANDROID
	}

}
