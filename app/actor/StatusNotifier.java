package actor;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import messages.DeviceNotificationMessage;
import messages.StatusChangeMessage;
import models.Device;
import models.Device.DevicePlatform;
import models.Room;
import models.Subscription;
import models.User;
import play.libs.Akka;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import dao.MongoDataSource;
import dao.RoomDAO;
import dao.SubscriptionDAO;
import dao.UserDAO;

/**
 * Receives the room id and the status that was changed.<br>
 * It retrieves all the users that are subscribed to the given room's status change and sends them a push notification
 * 
 * @author vinaypothnis
 *
 */
public class StatusNotifier extends UntypedActor {

	public static final String STATUS_CHANGE_MESSAGE_FORMAT = "%s status is now: %s";

	@Override
	public void onReceive(Object message) throws Exception {

		if (message instanceof StatusChangeMessage) {
			StatusChangeMessage origMessage = (StatusChangeMessage) message;
			MongoDataSource ds = MongoDataSource.getInstance();
			SubscriptionDAO subscriptionDao = new SubscriptionDAO(ds.getMongoTemplate());
			List<Subscription> subscriptions = subscriptionDao.getRoomSubscriptions(((StatusChangeMessage) message).getRoomId());

			if (subscriptions != null && !subscriptions.isEmpty()) {
				// get all the user records with their devices
				UserDAO userDao = new UserDAO(ds.getMongoTemplate());
				List<User> users = userDao.getUsers(getUserIds(subscriptions));

				// sort the devices based on platform
				List<Device> androidDevices = new ArrayList<Device>();
				List<Device> iosDevices = new ArrayList<Device>();
				sortDevicesByPlatform(users, androidDevices, iosDevices);

				// Get the room name
				RoomDAO roomDao = new RoomDAO(ds.getMongoTemplate());
				Room room = roomDao.getRoom(origMessage.getRoomId());

				String userNotification = format(STATUS_CHANGE_MESSAGE_FORMAT, room.getName(), origMessage.getStatus());

				// send the message to the individual actors to send out the notifications
				ActorRef androidNotifier = Akka.system().actorOf(Props.create(AndroidNotifier.class));
				DeviceNotificationMessage androidMessage = new DeviceNotificationMessage(userNotification, androidDevices);
				androidNotifier.tell(androidMessage, getSelf());

				ActorRef iosNotifier = Akka.system().actorOf(Props.create(IOSNotifier.class));
				DeviceNotificationMessage iosMessage = new DeviceNotificationMessage(userNotification, iosDevices);
				iosNotifier.tell(iosMessage, getSelf());

			}
		} else {
			unhandled(message);
		}

	}

	/**
	 * sort the devices based on platform
	 * 
	 * @param users
	 * @param androidDevices
	 * @param iosDevices
	 */
	private void sortDevicesByPlatform(List<User> users, List<Device> androidDevices, List<Device> iosDevices) {
		for (User user : users) {
			if (user.getDevices() != null && !user.getDevices().isEmpty()) {
				for (Device device : user.getDevices()) {
					if (DevicePlatform.ANDROID.equals(device.getPlatform())) {
						androidDevices.add(device);
					} else if (DevicePlatform.IOS.equals(device.getPlatform())) {
						iosDevices.add(device);
					}
				}
			}
		}
	}

	/**
	 * utility method to extract the user ids
	 * 
	 * @param subscriptions
	 * @return
	 */
	private List<String> getUserIds(List<Subscription> subscriptions) {
		List<String> userIds = new LinkedList<String>();
		for (Subscription subscription : subscriptions) {
			userIds.add(subscription.getUserId());
		}
		return userIds;
	}

}
