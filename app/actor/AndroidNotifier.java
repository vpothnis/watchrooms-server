package actor;

import static java.lang.String.format;
import messages.DeviceNotificationMessage;
import models.Device;
import models.GCMPayload;

import org.apache.http.HttpStatus;

import play.Logger;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Send the status change notification to the list of android devices specified in the message
 * 
 * @author vinaypothnis
 *
 */
public class AndroidNotifier extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {

		if (message instanceof DeviceNotificationMessage) {

			DeviceNotificationMessage devNotificationMessage = (DeviceNotificationMessage) message;
			Logger.debug(format("Received a message [%s] to send notifications to [%s] android devices", devNotificationMessage.getMessage(), devNotificationMessage.getDevices().size()));

			// TODO: Check that the number of registration ids or devices do not exceed 1000 limit from Google/Android
			String[] registrationIds = new String[((DeviceNotificationMessage) message).getDevices().size()];
			int i = 0;
			for (Device device : ((DeviceNotificationMessage) message).getDevices()) {
				registrationIds[i] = device.getDeviceId();
			}

			GCMPayload payload = new GCMPayload(false, registrationIds, ((DeviceNotificationMessage) message).getMessage());
			payload.setCollapseKey("WatchroomsStatusUpdate");
			
			WSRequestHolder wsRequestHolder = WS.url("https://android.googleapis.com/gcm/send");
			wsRequestHolder.setHeader("Authorization", "key=AIzaSyAJTRqFAvrF2WqFdMc5EB3XTw_Id6SzrAw");
			wsRequestHolder.setContentType("application/json");
			String jsonPayload = new ObjectMapper().writeValueAsString(payload);

			Logger.debug(format("Payload to Google Cloud Message Servers: [%s]", jsonPayload));
			Promise<WSResponse> promise = wsRequestHolder.post(jsonPayload);

			Logger.debug("Waiting for response from Google Cloud Message Servers");
			WSResponse response = promise.get(5000);
			Logger.debug("Received response from Google Cloud Message Servers");
			if (response.getStatus() == HttpStatus.SC_OK) {
				Logger.debug("Successfully sent notification request to Google Cloud Message Servers");
				Logger.debug(format("Response From GCM Servers: [%s]", response.getBody()));
			} else {
				Logger.error(format("Received error from GCM Servers. Response Code [%s] Response Body: [%s]", response.getStatus(), response.getBody()));
			}

		} else {
			unhandled(message);
		}
	}

}
