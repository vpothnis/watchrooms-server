package models;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class GCMPayload {

	@JsonProperty("dry_run")
	private boolean dryRun = false;

	@JsonProperty("registration_ids")
	private String[] registrationIds;

	@JsonProperty("data")
	private GCMData data;

	@JsonProperty("collapse_key")
	private String collapseKey;

	@JsonProperty("delay_while_idle")
	private boolean delayWhileIde = true;

	public GCMPayload(boolean dryRun, String[] registrationIds, String message) {
		super();
		this.dryRun = dryRun;
		this.registrationIds = registrationIds;
		this.data = new GCMData(message);
	}

	public GCMPayload() {
	}

	@Override
	public String toString() {
		return "GCMPayload [dryRun=" + dryRun + ", registrationIds=" + Arrays.toString(registrationIds) + ", data=" + data + ", collapseKey=" + collapseKey + ", delayWhileIde=" + delayWhileIde + "]";
	}

	public boolean isDryRun() {
		return dryRun;
	}

	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	public String[] getRegistrationIds() {
		return registrationIds;
	}

	public void setRegistrationIds(String[] registrationIds) {
		this.registrationIds = registrationIds;
	}

	public GCMData getData() {
		return data;
	}

	public void setData(GCMData data) {
		this.data = data;
	}

	public String getCollapseKey() {
		return collapseKey;
	}

	public void setCollapseKey(String collapseKey) {
		this.collapseKey = collapseKey;
	}

	public boolean isDelayWhileIde() {
		return delayWhileIde;
	}

	public void setDelayWhileIde(boolean delayWhileIde) {
		this.delayWhileIde = delayWhileIde;
	}

	public class GCMData {

		@JsonProperty("message")
		String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public GCMData(String message) {
			super();
			this.message = message;
		}

		@Override
		public String toString() {
			return "GCMData [message=" + message + "]";
		}

	}
}
