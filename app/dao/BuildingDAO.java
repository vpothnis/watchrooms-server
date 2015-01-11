package dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import models.Building;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class BuildingDAO {

	private MongoTemplate mongoTemplate;

	public BuildingDAO(MongoTemplate template) {
		this.mongoTemplate = template;
	}

	/**
	 * add the given building document to the collection. <br>
	 * generates the id for the building and returns the building object along
	 * with the id.
	 * 
	 * @param input
	 */
	public Building createBuilding(Building input) {
		// TODO: generate the building id here
		input.setBuildingId(UUID.randomUUID().toString());
		Long currentDate = Calendar.getInstance().getTimeInMillis();
		input.setCreateDate(currentDate);
		input.setUpdateDate(currentDate);
		mongoTemplate.insert(input, MongoDataSource.BUILDINGS_COLL);
		return input;
	}

	/**
	 * update the given building.
	 * 
	 * @param input
	 */
	public Building updateBuilding(Building input) {
		Long currentDate = Calendar.getInstance().getTimeInMillis();
		input.setUpdateDate(currentDate);
		mongoTemplate.updateFirst(new Query(where("_id").is(input.getBuildingId())),
		        new Update().set("name", input.getName()).set("address", input.getAddress()).set("updateDate", input.getUpdateDate()), Building.class);
		return input;
	}

	/**
	 * get the building for the given id
	 * 
	 * @param buildingId
	 */
	public Building getBuilding(String buildingId) {
		return mongoTemplate.findOne(new Query(where("_id").is(buildingId)), Building.class);
	}

	/**
	 * delete the building for the given id
	 * 
	 * @param buildingId
	 */
	public void deleteBuilding(String buildingId) {
		mongoTemplate.remove(new Query(where("_id").is(buildingId)), Building.class);
	}

	/**
	 * list the buildings in a paginated manner as specified
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public List<Building> listBuildings(int pageNumber, int pageSize) {
		Query query = new Query();
		query.skip((pageNumber - 1) * pageSize);
		query.limit(pageSize);
		query.with(new Sort(Direction.DESC, "createDate"));
		return mongoTemplate.find(query, Building.class);
	}

	/**
	 * get the count of all buildings
	 * 
	 * @return
	 */
	public Long getBuildingCount() {
		return mongoTemplate.count(new Query(), Building.class);
	}
}
