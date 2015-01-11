package controllers;

import static dao.MongoDataSource.DEFAULT_PAGE_NUMBER;
import static dao.MongoDataSource.DEFAULT_PAGE_SIZE;
import static java.lang.String.format;

import java.util.List;

import models.Building;
import models.BuildingList;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.BuildingDAO;
import dao.MongoDataSource;

/**
 * Handle the buildings related interactions
 * 
 * @author vinaypothnis
 *
 */
public class BuildingController extends Controller {

	/**
	 * List the buildings in a paginated manner
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public static Result listBuildings(Integer pageNumber, Integer pageSize) {
		try {
			pageNumber = pageNumber == null ? DEFAULT_PAGE_NUMBER : pageNumber;
			pageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
			Logger.debug(format("listing buildings with page number: [%s] and page size: [%s]", pageNumber, pageSize));
			BuildingDAO dao = getBuildingDao();
			Long count = dao.getBuildingCount();
			BuildingList result = new BuildingList();
			result.setCount(count);
			if (count > 0) {
				List<Building> buildings = dao.listBuildings(pageNumber, pageSize);
				result.setBuildings(buildings);
			}
			response().setContentType("application/json");
			return ok(new ObjectMapper().writeValueAsString(result));
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError(format("Unable to list the buildings with page number: [%s] and page size: [%s]", pageNumber, pageSize));
		}
	}

	/**
	 * Get the building for the specified id
	 * 
	 * @param buildingId
	 * @return
	 */
	public static Result getBuilding(String buildingId) {
		try {
			Logger.debug(format("retrieving building with id[%s]", buildingId));
			BuildingDAO dao = getBuildingDao();
			Building result = dao.getBuilding(buildingId);
			if (result != null) {
				response().setContentType("application/json");
				return ok(new ObjectMapper().writeValueAsString(result));
			} else {
				return notFound(format("building with id:[%s] not found", buildingId));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError(format("Unable to retrieve the building with id: [%s]", buildingId));
		}

	}

	/**
	 * Create a new building
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result createBuilding() {
		try {
			Building inputBuilding = new ObjectMapper().readValue(request().body().asJson().toString(), Building.class);
			Logger.debug("create building input: " + inputBuilding.toString());
			BuildingDAO dao = getBuildingDao();
			inputBuilding = dao.createBuilding(inputBuilding);
			Logger.debug("created building successfully: " + inputBuilding.toString());
			response().setContentType("application/json");
			return created(new ObjectMapper().writeValueAsString(inputBuilding));
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError("Unable to create the building.");
		}

	}

	/**
	 * Update the building with the specified values
	 * 
	 * @param buildingId
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateBuilding(String buildingId) {
		try {
			Building inputBuilding = new ObjectMapper().readValue(request().body().asJson().toString(), Building.class);
			Logger.debug("update building input: " + inputBuilding.toString());
			BuildingDAO dao = getBuildingDao();
			Building fromDB = dao.getBuilding(buildingId);
			if (fromDB != null) {
				inputBuilding.setBuildingId(fromDB.getBuildingId());
				inputBuilding = dao.updateBuilding(inputBuilding);
				Logger.debug("updated building successfully: " + inputBuilding.toString());
				response().setContentType("application/json");
				return ok(new ObjectMapper().writeValueAsString(inputBuilding));
			} else {
				return notFound(format("building with id:[%s] not found", buildingId));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError("Unable to update the building.");
		}
	}

	/**
	 * Delete the building with the specified id
	 * 
	 * @param buildingId
	 * @return
	 */
	public static Result deleteBuilding(String buildingId) {
		try {
			Logger.debug(format("deleting building with id[%s]", buildingId));
			BuildingDAO dao = getBuildingDao();
			dao.deleteBuilding(buildingId);
			Logger.debug(format("deleted building with id[%s]", buildingId));
			return ok();
		} catch (Exception e) {
			e.printStackTrace();
			return Results.internalServerError(format("Unable to delete the building with id: [%s]", buildingId));
		}
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
