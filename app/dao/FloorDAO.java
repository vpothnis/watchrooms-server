package dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import models.Floor;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class FloorDAO {

	private MongoTemplate mongoTemplate;

	public FloorDAO(MongoTemplate template) {
		this.mongoTemplate = template;
	}

	/**
	 * add the given floor document to the collection. <br>
	 * generates the id for the floor and returns the floor object along with
	 * the id.
	 * 
	 * @param input
	 */
	public Floor createFloor(Floor input) {
		// TODO: generate the floor id here
		input.setFloorId(UUID.randomUUID().toString());
		Long currentDate = Calendar.getInstance().getTimeInMillis();
		input.setCreateDate(currentDate);
		input.setUpdateDate(currentDate);
		mongoTemplate.insert(input, MongoDataSource.FLOORS_COLL);
		return input;
	}

	/**
	 * update the given floor.
	 * 
	 * @param input
	 */
	public Floor updateFloor(Floor input) {
		Long currentDate = Calendar.getInstance().getTimeInMillis();
		input.setUpdateDate(currentDate);
		mongoTemplate.updateFirst(new Query(where("_id").is(input.getFloorId())), new Update().set("name", input.getName()).set("updateDate", input.getUpdateDate()), Floor.class);
		return input;
	}

	/**
	 * get the floor for the given id
	 * 
	 * @param floorId
	 */
	public Floor getFloor(String floorId) {
		return mongoTemplate.findOne(new Query(where("_id").is(floorId)), Floor.class);
	}

	/**
	 * delete the floor for the given id
	 * 
	 * @param floorId
	 */
	public void deleteFloor(String floorId) {
		mongoTemplate.remove(new Query(where("_id").is(floorId)), Floor.class);
	}

	/**
	 * list the floors in a paginated manner as specified for the given building
	 * 
	 * @param buildingId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public List<Floor> listFloors(String buildingId, int pageNumber, int pageSize) {
		Query query = new Query();
		query.skip((pageNumber - 1) * pageSize);
		query.limit(pageSize);
		query.with(new Sort(Direction.DESC, "createDate"));
		query.addCriteria(where("buildingId").is(buildingId));
		return mongoTemplate.find(query, Floor.class);
	}

	/**
	 * get the count of all floors
	 * 
	 * @return
	 */
	public Long getFloorCount() {
		return mongoTemplate.count(new Query(), Floor.class);
	}

	/**
	 * count of floors in the given building
	 * 
	 * @param buildingId
	 * @return
	 */
	public Long getFloorCount(String buildingId) {
		return mongoTemplate.count(new Query(where("buildingId").is(buildingId)), Floor.class);
	}
}
