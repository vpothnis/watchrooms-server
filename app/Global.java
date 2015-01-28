import static java.lang.String.format;
import dao.MongoDataSource;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import actor.AndroidNotifier;
import actor.StatusNotifier;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * Handle application startup and shutdown tasks
 * 
 * @author vinaypothnis
 *
 */
public class Global extends GlobalSettings {

	private ActorRef statusNotifier = null;
	private ActorRef androidNotifier = null;
	private ActorRef iosNotifier = null;

	/**
	 * Create the actor references required for push notifications
	 */
	@Override
	public void onStart(Application arg0) {
		super.onStart(arg0);
		
		// startup the Mongo Data Source
		MongoDataSource.getInstance();

		// create the status notifier actor
		statusNotifier = Akka.system().actorOf(Props.create(StatusNotifier.class), "statusNotifier");
		Logger.debug(format("Created the StatusNotifier actor: [%s]", statusNotifier.path()));

		// create the android notifier
		androidNotifier = Akka.system().actorOf(Props.create(AndroidNotifier.class), "androidNotifier");
		Logger.debug(format("Created the AndroidNotifier actor: [%s]", androidNotifier.path()));

		// create the ios notifier
		iosNotifier = Akka.system().actorOf(Props.create(AndroidNotifier.class), "iosNotifier");
		Logger.debug(format("Created the IOSNotifier actor: [%s]", iosNotifier.path()));
	}

	@Override
	public void onStop(Application arg0) {
		// TODO Auto-generated method stub
		super.onStop(arg0);
	}

}
