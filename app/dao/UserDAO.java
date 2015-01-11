package dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import models.Subscription;
import models.User;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class UserDAO {

	private MongoTemplate mongoTemplate;

	public UserDAO(MongoTemplate template) {
		this.mongoTemplate = template;
	}

	/**
	 * add the given user document to the collection. <br>
	 * generates the id for the user and returns the user object along with the id.
	 * 
	 * @param input
	 */
	public User createUser(User input) {
		// TODO: generate the user id here
		input.setUserId(UUID.randomUUID().toString());
		Long currentDate = Calendar.getInstance().getTimeInMillis();
		input.setCreateDate(currentDate);
		input.setUpdateDate(currentDate);
		mongoTemplate.insert(input, MongoDataSource.USERS_COLL);
		return input;
	}

	/**
	 * update the given user.
	 * 
	 * @param input
	 */
	public User updateUser(User input) {
		Long currentDate = Calendar.getInstance().getTimeInMillis();
		input.setUpdateDate(currentDate);
		mongoTemplate.updateFirst(new Query(where("_id").is(input.getUserId())),
		        new Update().set("name", input.getName()).set("isFacility", input.getIsFacility()).set("updateDate", input.getUpdateDate()), User.class);
		return input;
	}

	/**
	 * get the user for the given id
	 * 
	 * @param userId
	 */
	public User getUser(String userId) {
		return mongoTemplate.findOne(new Query(where("_id").is(userId)), User.class);
	}

	/**
	 * delete the user for the given id
	 * 
	 * @param userId
	 */
	public void deleteUser(String userId) {
		mongoTemplate.remove(new Query(where("_id").is(userId)), User.class);
	}

	/**
	 * list the users in a paginated manner as specified for the given building
	 * 
	 * @param floorId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public List<User> listUsers(String floorId, int pageNumber, int pageSize) {
		Query query = new Query();
		query.skip((pageNumber - 1) * pageSize);
		query.limit(pageSize);
		query.with(new Sort(Direction.DESC, "createDate"));
		return mongoTemplate.find(query, User.class);
	}

	/**
	 * get all the subscriptions for the given user
	 * 
	 * @param userId
	 * @return
	 */
	public List<Subscription> getUserSubscriptions(String userId) {
		Query query = new Query(where("userId").is(userId));
		return mongoTemplate.find(query, Subscription.class);
	}

	/**
	 * get the count of all users
	 * 
	 * @return
	 */
	public Long getUserCount() {
		return mongoTemplate.count(new Query(), User.class);
	}

	public User getUserByEmailId(String emailId) {
		return mongoTemplate.findOne(new Query(where("emailId").is(emailId)), User.class);
    }

}
