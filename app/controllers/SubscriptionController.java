package controllers;

import static java.lang.String.format;

import java.util.List;

import models.Room;
import models.Subscription;
import models.SubscriptionList;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.MongoDataSource;
import dao.RoomDAO;
import dao.SubscriptionDAO;
import dao.UserDAO;

/**
 * Handle the subscription related interactions
 * 
 * @author vinaypothnis
 *
 */
public class SubscriptionController extends Controller {

	/**
	 * Subscribe the given user for the status updates of the given room
	 * 
	 * @param roomId
	 * @param userId
	 * @return
	 */
	public static Result subscribe(String roomId, String userId) {
		return subscribeOrUnsubscribe(roomId, userId, false);
	}

	/**
	 * Unsubscribe the given user from the status updates of the given room
	 * 
	 * @param roomId
	 * @param userId
	 * @return
	 */
	public static Result unsubscribe(String roomId, String userId) {
		return subscribeOrUnsubscribe(roomId, userId, true);
	}

	/**
	 * utility method
	 * 
	 * @param roomId
	 * @param userId
	 * @return
	 */
	public static Result subscribeOrUnsubscribe(String roomId, String userId, Boolean unsubscribe) {
		try {
			// verify that the room exists
			Room room = getRoomDao().getRoom(roomId);
			if (room != null) {
				// verify that the user exists
				UserDAO userDao = getUserDao();
				User user = userDao.getUser(userId);
				if (user != null) {
					if (unsubscribe) {
						// unsubscribe
						getSubscriptionDao().removeSubscription(roomId, userId);
					} else {
						// subscribe
						getSubscriptionDao().addSubscription(roomId, userId);
					}
					// return the user's subscriptions
					SubscriptionList result = new SubscriptionList();
					List<Subscription> subscriptions = userDao.getUserSubscriptions(userId);
					result.setCount(Long.valueOf(subscriptions.size()));
					result.setSubscriptions(subscriptions);
					response().setContentType("application/json");
					return ok(new ObjectMapper().writeValueAsString(result));
				} else {
					return notFound(format("user with id:[%s] not found", userId));
				}
			} else {
				return notFound(format("room with id:[%s] not found", roomId));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError(format("Unable to subscribe/unsubscribe the user: [%s] for the room with id: [%s]", userId, roomId));
		}

	}

	/**
	 * utility method
	 * 
	 * @return
	 */
	private static SubscriptionDAO getSubscriptionDao() {
		MongoDataSource dataSource = MongoDataSource.getInstance();
		SubscriptionDAO dao = new SubscriptionDAO(dataSource.getMongoTemplate());
		return dao;
	}

	/**
	 * utility method
	 * 
	 * @return
	 */
	private static RoomDAO getRoomDao() {
		MongoDataSource dataSource = MongoDataSource.getInstance();
		RoomDAO dao = new RoomDAO(dataSource.getMongoTemplate());
		return dao;
	}

	/**
	 * utility method
	 * 
	 * @return
	 */
	private static UserDAO getUserDao() {
		MongoDataSource dataSource = MongoDataSource.getInstance();
		UserDAO dao = new UserDAO(dataSource.getMongoTemplate());
		return dao;
	}

}
