package controllers;

import static dao.MongoDataSource.DEFAULT_PAGE_NUMBER;
import static dao.MongoDataSource.DEFAULT_PAGE_SIZE;
import static java.lang.String.format;

import java.util.List;

import models.Building;
import models.Floor;
import models.FloorList;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.BuildingDAO;
import dao.FloorDAO;
import dao.MongoDataSource;

/**
 * Handle the floors related interactions
 * 
 * @author vinaypothnis
 *
 */
public class FloorController extends Controller {

	/**
	 * List the floors in a paginated manner
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public static Result listFloors(String buildingId, Integer pageNumber, Integer pageSize) {
		try {
			pageNumber = pageNumber == null ? DEFAULT_PAGE_NUMBER : pageNumber;
			pageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
			Logger.debug(format("listing floors on building: [%s] with page number: [%s] and page size: [%s]", buildingId, pageNumber, pageSize));
			FloorDAO dao = getFloorDao();
			Long count = dao.getFloorCount(buildingId);
			FloorList result = new FloorList();
			result.setCount(count);
			if (count > 0) {
				List<Floor> floors = dao.listFloors(buildingId, pageNumber, pageSize);
				result.setFloors(floors);
			}
			response().setContentType("application/json");
			return ok(new ObjectMapper().writeValueAsString(result));
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError(format("Unable to list the floors on building: [%s] with page number: [%s] and page size: [%s]", buildingId, pageNumber, pageSize));
		}
	}

	/**
	 * Get the floor for the specified id
	 * 
	 * @param floorId
	 * @return
	 */
	public static Result getFloor(String floorId) {
		try {
			Logger.debug(format("retrieving floor with id[%s]", floorId));
			FloorDAO dao = getFloorDao();
			Floor result = dao.getFloor(floorId);
			if (result != null) {
				response().setContentType("application/json");
				return ok(new ObjectMapper().writeValueAsString(result));
			} else {
				return notFound(format("floor with id:[%s] not found", floorId));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError(format("Unable to retrieve the floor with id: [%s]", floorId));
		}

	}

	/**
	 * Create a new floor
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result createFloor() {
		try {
			Floor inputFloor = new ObjectMapper().readValue(request().body().asJson().toString(), Floor.class);
			Logger.debug("create floor input: " + inputFloor.toString());

			// check if the building is available
			BuildingDAO buildingDao = getBuildingDao();
			Building building = buildingDao.getBuilding(inputFloor.getBuildingId());
			if (building != null) {
				FloorDAO dao = getFloorDao();
				inputFloor = dao.createFloor(inputFloor);
				Logger.debug("created floor successfully: " + inputFloor.toString());
				response().setContentType("application/json");
				return created(new ObjectMapper().writeValueAsString(inputFloor));
			} else {
				Logger.debug(format("Could not create floor. Parent building: [%s] not found", inputFloor.getBuildingId()));
				return badRequest(format("Could not create floor. Parent building: [%s] not found", inputFloor.getBuildingId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError("Unable to create the floor.");
		}

	}

	/**
	 * Update the floor with the specified values
	 * 
	 * @param floorId
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateFloor(String floorId) {
		try {
			Floor inputFloor = new ObjectMapper().readValue(request().body().asJson().toString(), Floor.class);
			Logger.debug("update floor input: " + inputFloor.toString());
			FloorDAO dao = getFloorDao();
			Floor fromDB = dao.getFloor(floorId);
			if (fromDB != null) {
				// copy over the data from input
				fromDB.setName(inputFloor.getName());
				fromDB = dao.updateFloor(fromDB);
				Logger.debug("updated floor successfully: " + fromDB.toString());
				response().setContentType("application/json");
				return ok(new ObjectMapper().writeValueAsString(fromDB));
			} else {
				return notFound(format("floor with id:[%s] not found", floorId));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError("Unable to update the floor.");
		}
	}

	/**
	 * Delete the floor with the specified id
	 * 
	 * @param floorId
	 * @return
	 */
	public static Result deleteFloor(String floorId) {
		try {
			Logger.debug(format("deleting floor with id[%s]", floorId));
			FloorDAO dao = getFloorDao();
			dao.deleteFloor(floorId);
			Logger.debug(format("deleted floor with id[%s]", floorId));
			return ok();
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError(format("Unable to delete the floor with id: [%s]", floorId));
		}
	}

	/**
	 * utility method
	 * 
	 * @return
	 */
	private static FloorDAO getFloorDao() {
		MongoDataSource dataSource = MongoDataSource.getInstance();
		FloorDAO dao = new FloorDAO(dataSource.getMongoTemplate());
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
