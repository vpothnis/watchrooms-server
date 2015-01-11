package controllers;

import static java.lang.String.format;

import java.io.IOException;

import models.Device;
import models.User;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dao.MongoDataSource;
import dao.UserDAO;

/**
 * Handle the device related interactions
 * 
 * @author vinaypothnis
 *
 */
public class DeviceController extends Controller {

	private static final String REGISTERING = "registering";
	private static final String UNREGISTERING = "unregistering";

	/**
	 * Register the given device for the given user
	 * 
	 * @param userId
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result registerDevice(String userId) {
		try {
			return doWork(userId, REGISTERING);
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError(format("Unable to register the device for the user with id: [%s]", userId));
		}

	}

	/**
	 * Unregister the given device from the given user
	 * 
	 * @param userId
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result unregisterDevice(String userId) {
		try {
			return doWork(userId, UNREGISTERING);
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError(format("Unable to unregister the device for the user with id: [%s]", userId));
		}
	}

	/**
	 * Utility method to actually register/unregister the given device with the user
	 * 
	 * @param userId
	 * @param operation
	 * @return
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	private static Result doWork(String userId, String operation) throws IOException, JsonParseException, JsonMappingException, JsonProcessingException {
		// the input device details
		Device inputDevice = new ObjectMapper().readValue(request().body().asJson().toString(), Device.class);
		Logger.debug(format("%s input device: [%s]", operation, inputDevice.toString()));
		if (inputDevice.getDeviceId() != null && inputDevice.getPlatform() != null) {
			// get the user from db
			UserDAO userDao = getUserDao();
			User fromDB = userDao.getUser(userId);
			if (fromDB != null) {
				// add/remove the device from the user's device list
				if (REGISTERING.equalsIgnoreCase(operation)) {
					fromDB.addDevice(inputDevice);
				} else {
					fromDB.removeDevice(inputDevice);
				}
				// update the user in the database
				fromDB = userDao.updateUser(fromDB);
				Logger.debug(format("device %s successfull: [%s]", operation, fromDB.toString()));

				// return the updated user document
				response().setContentType("application/json");
				return ok(new ObjectMapper().writeValueAsString(fromDB));

			} else {
				return badRequest(format("Unable to find user with id:[%s]", userId));
			}
		} else {
			return badRequest(format("device id not provided"));
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

}
