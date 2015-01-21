package controllers;

import static dao.MongoDataSource.DEFAULT_PAGE_NUMBER;
import static dao.MongoDataSource.DEFAULT_PAGE_SIZE;
import static java.lang.String.format;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import messages.StatusChangeMessage;
import models.Building;
import models.Room;
import models.Room.RoomStatus;
import models.RoomList;
import models.Subscription;
import play.Logger;
import play.libs.Akka;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import akka.actor.ActorRef;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.BuildingDAO;
import dao.MongoDataSource;
import dao.RoomDAO;
import dao.UserDAO;

/**
 * Handle the rooms related interactions
 * 
 * @author vinaypothnis
 *
 */
public class RoomController extends Controller {

	/**
	 * List the rooms in a paginated manner.<br>
	 * If the useId is provided, then get his subscriptions and then decorate the room list with the user's subscription status.
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param floorId
	 * @param userId
	 * @return
	 */
	public static Result listRooms(String floorId, Integer pageNumber, Integer pageSize, String userId) {
		try {
			pageNumber = pageNumber == null ? DEFAULT_PAGE_NUMBER : pageNumber;
			pageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
			Logger.debug(format("listing rooms on floor: [%s] with page number: [%s] and page size: [%s]", floorId, pageNumber, pageSize));
			RoomDAO dao = getRoomDao();
			Long count = dao.getRoomCountForFloor(floorId);
			RoomList result = new RoomList();
			result.setCount(count);
			if (count > 0) {
				List<Room> rooms = dao.listRooms(floorId, pageNumber, pageSize);
				result.setRooms(rooms);
			}

			if (result.getRooms() != null && !result.getRooms().isEmpty()) {
				// if the userId is specified, then get his subscriptions and decorate the result list with the user's subscriptions
				if (userId != null && !userId.isEmpty()) {
					List<Subscription> subscriptions = getUserDao().getUserSubscriptions(userId);
					if (subscriptions != null && !subscriptions.isEmpty()) {
						Set<String> roomIdSet = new HashSet<String>();
						for (Subscription subscription : subscriptions) {
							roomIdSet.add(subscription.getRoomId());
						}
						// set the subscription status
						for (Room room : result.getRooms()) {
							if (roomIdSet.contains(room.getRoomId())) {
								room.setSubscribed(true);
							}
						}
					}
				}
			}

			response().setContentType("application/json");
			return ok(new ObjectMapper().writeValueAsString(result));
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError(format("Unable to list the rooms on floor: [%s] with page number: [%s] and page size: [%s]", floorId, pageNumber, pageSize));
		}
	}

	/**
	 * Get the room for the specified id
	 * 
	 * @param roomId
	 * @return
	 */
	public static Result getRoom(String roomId) {
		try {
			Logger.debug(format("retrieving room with id[%s]", roomId));
			RoomDAO dao = getRoomDao();
			Room result = dao.getRoom(roomId);
			if (result != null) {
				response().setContentType("application/json");
				return ok(new ObjectMapper().writeValueAsString(result));
			} else {
				return notFound(format("room with id:[%s] not found", roomId));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError(format("Unable to retrieve the room with id: [%s]", roomId));
		}

	}

	/**
	 * Create a new room
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result createRoom() {
		try {
			Room inputRoom = new ObjectMapper().readValue(request().body().asJson().toString(), Room.class);
			Logger.debug("create room input: " + inputRoom.toString());

			// check if the floor is available and that it belongs to the
			// building specified in the input
			BuildingDAO floorDao = getBuildingDao();
			Building floor = floorDao.getBuilding(inputRoom.getBuildingId());
			if (floor != null && floor.getBuildingId().equalsIgnoreCase(inputRoom.getBuildingId())) {
				RoomDAO dao = getRoomDao();
				inputRoom = dao.createRoom(inputRoom);
				Logger.debug("created room successfully: " + inputRoom.toString());
				response().setContentType("application/json");
				return created(new ObjectMapper().writeValueAsString(inputRoom));
			} else {
				Logger.debug(format("Could not create room. Parent floor: [%s] not found OR floor does not belong to given buildingId: [%s]", inputRoom.getFloorId(), inputRoom.getBuildingId()));
				return badRequest(format("Could not create room. Parent floor: [%s] not found OR floor does not belong to given buildingId: [%s]", inputRoom.getFloorId(), inputRoom.getBuildingId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError("Unable to create the room.");
		}

	}

	/**
	 * Update the room with the specified values
	 * 
	 * @param roomId
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateRoom(String roomId) {
		try {
			Room inputRoom = new ObjectMapper().readValue(request().body().asJson().toString(), Room.class);
			Logger.debug("update room input: " + inputRoom.toString());
			RoomDAO dao = getRoomDao();
			Room fromDB = dao.getRoom(roomId);
			if (fromDB != null) {
				// copy over the data from input
				if (inputRoom.getName() != null) {
					fromDB.setName(inputRoom.getName());
				}
				if (inputRoom.getGender() != null) {
					fromDB.setGender(inputRoom.getGender());
				}
				if (inputRoom.getCurrentStatus() != null) {
					fromDB.setCurrentStatus(inputRoom.getCurrentStatus());
				}
				fromDB = dao.updateRoom(fromDB);
				Logger.debug("updated room successfully: " + fromDB.toString());
				response().setContentType("application/json");
				return ok(new ObjectMapper().writeValueAsString(fromDB));
			} else {
				return notFound(format("room with id:[%s] not found", roomId));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError("Unable to update the room.");
		}
	}

	/**
	 * Update the room's status with the given value
	 * 
	 * @param roomId
	 * @param status
	 * @return
	 */
	public static Result updateRoomStatus(String roomId, String status) {
		try {
			Logger.debug(format("trying to update room [%s] with status: [%s] ", roomId, status));
			RoomDAO dao = getRoomDao();
			Room fromDB = dao.getRoom(roomId);
			if (fromDB != null) {
				fromDB.setCurrentStatus(RoomStatus.valueOf(status));
				fromDB = dao.updateRoom(fromDB);
				Logger.debug("updated room successfully: " + fromDB.toString());

				// trigger the push notifications of the status update.
				Akka.system().actorSelection("/user/statusNotifier").tell(new StatusChangeMessage(roomId, status), ActorRef.noSender());

				response().setContentType("application/json");
				return ok(new ObjectMapper().writeValueAsString(fromDB));
			} else {
				return notFound(format("room with id:[%s] not found", roomId));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError("Unable to update the room status.");
		}
	}

	/**
	 * Delete the room with the specified id
	 * 
	 * @param roomId
	 * @return
	 */
	public static Result deleteRoom(String roomId) {
		try {
			Logger.debug(format("deleting room with id[%s]", roomId));
			RoomDAO dao = getRoomDao();
			dao.deleteRoom(roomId);
			Logger.debug(format("deleted room with id[%s]", roomId));
			return ok();
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError(format("Unable to delete the room with id: [%s]", roomId));
		}
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

	/**
	 * utility method
	 * 
	 * @return
	 */
	private static BuildingDAO getBuildingDao() {
		MongoDataSource dataSource = MongoDataSource.getInstance();
		BuildingDAO dao = new BuildingDAO(dataSource.getMongoTemplate());
		return dao;
	}
}
