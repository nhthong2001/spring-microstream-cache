package vn.elca.demo.config;
import com.mongodb.*;
import org.apache.log4j.Logger;

public class MongoFactory {

    private static Logger log = Logger.getLogger(MongoFactory.class);

    private static MongoClient mongo;

    private MongoFactory() { }

    // Returns a mongo instance.
    public static MongoClient getMongo() {
        int port_no = 27017;
        String hostname = "localhost";
        if (mongo == null) {
            try {
                mongo = new MongoClient(hostname, port_no);
            } catch (MongoException ex) {
                log.error(ex);
            }
        }
        return mongo;
    }

    // Fetches the mongo database.
    public static DB getDB(String db_name) {
        return getMongo().getDB(db_name);
    }

    // Fetches the collection from the mongo database.
    public static DBCollection getCollection(String db_name, String db_collection) {
        return getDB(db_name).getCollection(db_collection);
    }
}
