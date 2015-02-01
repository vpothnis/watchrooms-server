package dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import models.Room;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class RoomDAO {

	private MongoTemplate mongoTemplate;

	public RoomDAO(MongoTemplate template) {
		this.mongoTemplate = template;
	}

	/**
	 * add the given room document to the collection. <br>
	 * generates the id for the room and returns the room object along with the id.
	 * 
	 * @param input
	 */
	public Room createRoom(Room input) {
		// TODO: generate the room id here
		input.setRoomId(UUID.randomUUID().toString());
		Long currentDate = Calendar.getInstance().getTimeInMillis();
		input.setCreateDate(currentDate);
		input.setUpdateDate(currentDate);
		mongoTemplate.insert(input, MongoDataSource.ROOMS_COLL);
		return input;
	}

	/**
	 * update the given room.
	 * 
	 * @param input
	 */
	public Room updateRoom(Room input) {
		Long currentDate = Calendar.getInstance().getTimeInMillis();
		input.setUpdateDate(currentDate);
		mongoTemplate.updateFirst(new Query(where("_id").is(input.getRoomId())),
		        new Update().set("name", input.getName()).set("gender", input.getGender()).set("currentStatus", input.getCurrentStatus()).set("updateDate", input.getUpdateDate()), Room.class);
		return input;
	}

	/**
	 * get the room for the given id
	 * 
	 * @param roomId
	 */
	public Room getRoom(String roomId) {
		return mongoTemplate.findOne(new Query(where("_id").is(roomId)), Room.class);
	}

	/**
	 * get the rooms for the given list of ids
	 * 
	 * @param roomIds
	 */
	public List<Room> getRooms(List<String> roomIds) {
		return mongoTemplate.find(new Query(where("_id").in(roomIds)), Room.class);
	}

	/**
	 * delete the room for the given id
	 * 
	 * @param roomId
	 */
	public void deleteRoom(String roomId) {
		mongoTemplate.remove(new Query(where("_id").is(roomId)), Room.class);
	}

	/**
	 * list the rooms in a paginated manner as specified for the given building
	 * 
	 * @param floorId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public List<Room> listRooms(String floorId, int pageNumber, int pageSize) {
		Query query = new Query();
		query.skip((pageNumber - 1) * pageSize);
		query.limit(pageSize);
		query.with(new Sort(Direction.DESC, "createDate"));
		query.addCriteria(where("floorId").is(floorId));
		return mongoTemplate.find(query, Room.class);
	}

	/**
	 * get the count of all rooms
	 * 
	 * @return
	 */
	public Long getRoomCount() {
		return mongoTemplate.count(new Query(), Room.class);
	}

	/**
	 * count of rooms in the given building
	 * 
	 * @param buildingId
	 * @return
	 */
	public Long getRoomCountForBuilding(String buildingId) {
		return mongoTemplate.count(new Query(where("buildingId").is(buildingId)), Room.class);
	}

	/**
	 * count of rooms in the given floor
	 * 
	 * @param buildingId
	 * @return
	 */
	public Long getRoomCountForFloor(String floorId) {
		return mongoTemplate.count(new Query(where("floorId").is(floorId)), Room.class);
	}
}
