package controllers;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import models.Room;
import models.RoomList;
import models.Subscription;
import models.User;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.MongoDataSource;
import dao.RoomDAO;
import dao.UserDAO;

/**
 * Handle the users related interactions
 * 
 * @author vinaypothnis
 *
 */
public class UserController extends Controller {

	/**
	 * Get the user for the specified id
	 * 
	 * @param userId
	 * @return
	 */
	public static Result getUser(String userId) {
		try {
			Logger.debug(format("retrieving user with id[%s]", userId));
			UserDAO dao = getUserDao();
			User result = dao.getUser(userId);
			if (result != null) {
				response().setContentType("application/json");
				return ok(new ObjectMapper().writeValueAsString(result));
			} else {
				return notFound(format("user with id:[%s] not found", userId));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError(format("Unable to retrieve the user with id: [%s]", userId));
		}

	}

	/**
	 * Get the user for the specified id
	 * 
	 * @param emailId
	 * @return
	 */
	public static Result getUserByEmailId(String emailId) {
		try {
			Logger.debug(format("retrieving user with email id[%s]", emailId));
			UserDAO dao = getUserDao();
			User result = dao.getUserByEmailId(emailId);
			if (result != null) {
				response().setContentType("application/json");
				return ok(new ObjectMapper().writeValueAsString(result));
			} else {
				return notFound(format("user with email id:[%s] not found", emailId));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError(format("Unable to retrieve the user with email id: [%s]", emailId));
		}

	}

	/**
	 * Get the subscriptions for the specified user
	 * 
	 * @param userId
	 * @return
	 */
	public static Result getUserSubscriptions(String userId) {
		try {
			Logger.debug(format("retrieving user with id[%s]", userId));
			UserDAO dao = getUserDao();
			RoomDAO roomDao = getRoomDao();
			User fromDB = dao.getUser(userId);
			RoomList result = new RoomList();
			if (fromDB != null) {
				List<Subscription> subscriptions = dao.getUserSubscriptions(userId);
				if ( subscriptions != null && !subscriptions.isEmpty() ){
					
					// fetch the rooms
					List<String> roomIds = new ArrayList<String>();
					subscriptions.forEach((sub) -> roomIds.add(sub.getRoomId()));
					List<Room> rooms = roomDao.getRooms(roomIds); 
					
					// set the subscribed status
					rooms.forEach((room) -> room.setSubscribed(true));
					
					// return the results
					result.setCount(Long.valueOf(rooms.size()));
					result.setRooms(rooms);
				}
				response().setContentType("application/json");		
				return ok(new ObjectMapper().writeValueAsString(result));
			} else {
				return notFound(format("user with id:[%s] not found", userId));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError(format("Unable to retrieve subscriptions for user with id: [%s]", userId));
		}

	}

	/**
	 * Create a new user
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result createUser() {
		try {
			User inputUser = new ObjectMapper().readValue(request().body().asJson().toString(), User.class);
			Logger.debug("create user input: " + inputUser.toString());
			UserDAO dao = getUserDao();
			inputUser = dao.createUser(inputUser);
			Logger.debug("created user successfully: " + inputUser.toString());
			response().setContentType("application/json");
			return created(new ObjectMapper().writeValueAsString(inputUser));
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError("Unable to create the user.");
		}

	}

	/**
	 * Update the user with the specified values
	 * 
	 * @param userId
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateUser(String userId) {
		try {
			User inputUser = new ObjectMapper().readValue(request().body().asJson().toString(), User.class);
			Logger.debug("update user input: " + inputUser.toString());
			UserDAO dao = getUserDao();
			User fromDB = dao.getUser(userId);
			if (fromDB != null) {
				// copy over the data from input
				if (inputUser.getName() != null) {
					fromDB.setName(inputUser.getName());
				}
				if (inputUser.getIsFacility() != null) {
					fromDB.setIsFacility(inputUser.getIsFacility());
				}
				fromDB = dao.updateUser(fromDB);
				Logger.debug("updated user successfully: " + fromDB.toString());
				response().setContentType("application/json");
				return ok(new ObjectMapper().writeValueAsString(fromDB));
			} else {
				return notFound(format("user with id:[%s] not found", userId));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError("Unable to update the user.");
		}
	}

	/**
	 * Delete the user with the specified id
	 * 
	 * @param userId
	 * @return
	 */
	public static Result deleteUser(String userId) {
		try {
			Logger.debug(format("deleting user with id[%s]", userId));
			UserDAO dao = getUserDao();
			dao.deleteUser(userId);
			Logger.debug(format("deleted user with id[%s]", userId));
			return ok();
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError(format("Unable to delete the user with id: [%s]", userId));
		}
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

}
