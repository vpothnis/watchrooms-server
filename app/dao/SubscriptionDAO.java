package dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import models.Subscription;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

public class SubscriptionDAO {

	private MongoTemplate mongoTemplate;

	public SubscriptionDAO(MongoTemplate template) {
		this.mongoTemplate = template;
	}

	/**
	 * add a subscription for the user to the given room
	 * 
	 * @param roomId
	 * @param userId
	 */
	public void addSubscription(String roomId, String userId) {
		mongoTemplate.insert(new Subscription(roomId, userId), MongoDataSource.SUBSCRIPTIONS_COLL);
	}

	/**
	 * remove the subscription for the user from the given room
	 * 
	 * @param roomId
	 * @param userId
	 */
	public void removeSubscription(String roomId, String userId) {
		Query query = new Query(where("roomId").is(roomId).and("userId").is(userId));
		mongoTemplate.remove(query, MongoDataSource.SUBSCRIPTIONS_COLL);
	}

	/**
	 * get all the subscriptions for the given room
	 * 
	 * @param roomId
	 * @return
	 */
	public List<Subscription> getRoomSubscriptions(String roomId) {
		Query query = new Query(where("roomId").is(roomId));
		return mongoTemplate.find(query, Subscription.class);
	}
}
