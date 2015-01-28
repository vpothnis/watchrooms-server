package dao;

import static java.lang.String.format;

import java.net.UnknownHostException;

import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import play.Configuration;
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

	private static String mongoHost;
	private static Integer mongoPort;
	private static String mongoUser;
	private static String mongoPassword;
	private static String mongoDatabase;
	private static String mongoAuthDatabase;
	private static Integer mongoMaxConnPerHost = 10;
	private static Integer mongoMaxWaitTime = 1000;
	private static Integer mongoMaxConnectTimeout = 1000;
	private static Integer mongoMaxSocketTimeout = 2000;

	// defaults
	private static final Integer DEFAULT_MAX_CONN_PER_HOST = 10;
	private static final Integer DEFAULT_MAX_WAIT_TIME = 1000;
	private static final Integer DEFAULT_MAX_CONNECT_TIMEOUT = 1000;
	private static final Integer DEFAULT_MAX_SOCKET_TIMEOUT = 2000;

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
			mongoHost = Configuration.root().getString("mongo.host");
			mongoPort = Configuration.root().getInt("mongo.port");
			mongoUser = Configuration.root().getString("mongo.user");
			mongoPassword = Configuration.root().getString("mongo.password");
			mongoDatabase = Configuration.root().getString("mongo.database");
			mongoAuthDatabase = Configuration.root().getString("mongo.authDatabase");
			mongoMaxConnPerHost = Configuration.root().getInt("mongo.maxConnectionsPerHost", DEFAULT_MAX_CONN_PER_HOST);
			mongoMaxWaitTime = Configuration.root().getInt("mongo.maxWaitTime", DEFAULT_MAX_WAIT_TIME);
			mongoMaxConnectTimeout = Configuration.root().getInt("mongo.connectTimeout", DEFAULT_MAX_CONNECT_TIMEOUT);
			mongoMaxSocketTimeout = Configuration.root().getInt("mongo.socketTimeout", DEFAULT_MAX_SOCKET_TIMEOUT);

			logConfigs();

			MongoClientOptions clientOptions = MongoClientOptions.builder().connectionsPerHost(mongoMaxConnPerHost).maxWaitTime(mongoMaxWaitTime).connectTimeout(mongoMaxConnectTimeout)
			        .socketTimeout(mongoMaxSocketTimeout).build();
			ServerAddress serverAddress = new ServerAddress(mongoHost, mongoPort);
			mongoClient = new MongoClient(serverAddress, clientOptions);
			mongoClient.setWriteConcern(WriteConcern.JOURNALED);

			UserCredentials userCredentials = new UserCredentials(mongoUser, mongoPassword);
			MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClient, mongoDatabase, userCredentials, mongoAuthDatabase);
			mongoTemplate = new MongoTemplate(mongoDbFactory);
		} catch (UnknownHostException e) {
			Logger.error("Unable to create mongo data source", e);
		}
	}

	private static void logConfigs() {
		Logger.debug(format("Mongo Configuration Parameters:"));
		Logger.debug(format("Mongo Host: %s", mongoHost));
		Logger.debug(format("Mongo Port: %s", mongoPort));
		Logger.debug(format("Mongo User: %s", mongoUser));
		Logger.debug(format("Mongo Password: %s", "xxxx"));
		Logger.debug(format("Mongo Max Conn Per Host: %s", mongoMaxConnPerHost));
		Logger.debug(format("Mongo Max Wait Time: %s", mongoMaxWaitTime));
		Logger.debug(format("Mongo Max Connect Timeout: %s", mongoMaxConnectTimeout));
		Logger.debug(format("Mongo Maz Socket Timeout: %s", mongoMaxSocketTimeout));
	}

	public MongoClient getMongoClient() {
		return mongoClient;
	}

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

}
