package dao;

import java.net.UnknownHostException;

import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import play.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

public class MongoDataSource {

	private static MongoClient mongoClient;
	private static MongoDataSource mongoDataSource;

	private static MongoTemplate mongoTemplate;

	// database name
	public static final String WATCHROOMS_DB = "watchrooms";

	// collections
	public static final String BUILDINGS_COLL = "buildings";
	public static final String FLOORS_COLL = "floors";
	public static final String ROOMS_COLL = "rooms";
	public static final String USERS_COLL = "users";
	public static final String SUBSCRIPTIONS_COLL = "subscriptions";

	// default pagination details
	public static final Integer DEFAULT_PAGE_NUMBER = 0;
	public static final Integer DEFAULT_PAGE_SIZE = 10;

	/**
	 * private constructor
	 */
	private MongoDataSource() {

	}

	public static synchronized MongoDataSource getInstance() {
		if (mongoDataSource != null) {
			return mongoDataSource;
		} else {
			initMongoDataSource();
			mongoDataSource = new MongoDataSource();
			return mongoDataSource;
		}
	}

	private static void initMongoDataSource() {
		try {
			MongoClientOptions clientOptions = MongoClientOptions.builder().connectionsPerHost(100).maxWaitTime(2000).connectTimeout(2000).socketTimeout(10000).build();
			ServerAddress serverAddress = new ServerAddress("localhost", 27017);
			mongoClient = new MongoClient(serverAddress, clientOptions);
			mongoClient.setWriteConcern(WriteConcern.JOURNALED);

			UserCredentials userCredentials = new UserCredentials("watchroom_app", "w@tchr00m_@pp");
			MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClient, WATCHROOMS_DB, userCredentials, WATCHROOMS_DB);
			mongoTemplate = new MongoTemplate(mongoDbFactory);
		} catch (UnknownHostException e) {
			Logger.error("Unable to create mongo data source", e);
		}
	}

	public MongoClient getMongoClient() {
		return mongoClient;
	}

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

}
