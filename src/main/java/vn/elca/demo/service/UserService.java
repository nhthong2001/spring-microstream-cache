package vn.elca.demo.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import one.microstream.memory.MemoryStatistics;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.elca.demo.config.MongoFactory;
import vn.elca.demo.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserService {

    static String db_name = "mydb", db_collection = "mycollection";

    // Fetch all users from the mongo database.
    public List<User> getAll() {
        List<User> user_list = new ArrayList<>();
        DBCollection coll = MongoFactory.getCollection(db_name, db_collection);

        // Fetching cursor object for iterating on the database records.
        DBCursor cursor = coll.find();
        while(cursor.hasNext()) {
            DBObject dbObject = cursor.next();

            User user = new User();
            user.setId(dbObject.get("id").toString());
            user.setName(dbObject.get("name").toString());

            // Adding the user details to the list.
            user_list.add(user);
        }
        return user_list;
    }

    public Set<User> getSet1() {
        Set<User> user_list = new HashSet<>();
        DBCollection coll = MongoFactory.getCollection(db_name, db_collection);

        DBCursor cursor = coll.find();
        while(cursor.hasNext()) {
            DBObject dbObject = cursor.next();

            User user = new User();
            user.setId(dbObject.get("id").toString());
            user.setName(dbObject.get("name").toString());

            String id = user.getId();
            if (id.equalsIgnoreCase("1") || id.equalsIgnoreCase("2") || id.equalsIgnoreCase("3")) {
                user_list.add(user);
            }
        }
        return user_list;
    }

    public List<User> getList2() {
        List<User> user_list = new ArrayList<User>();
        DBCollection coll = MongoFactory.getCollection(db_name, db_collection);

        DBCursor cursor = coll.find();
        while(cursor.hasNext()) {
            DBObject dbObject = cursor.next();

            User user = new User();
            user.setId(dbObject.get("id").toString());
            user.setName(dbObject.get("name").toString());

            String id = user.getId();
            if (id.equalsIgnoreCase("1") || id.equalsIgnoreCase("4") || id.equalsIgnoreCase("6")) {
                user_list.add(user);
            }
        }
        return user_list;
    }

//    // Add a new user to the mongo database.
//    public Boolean add(User user) {
//        boolean output = false;
//        Random ran = new Random();
//        log.debug("Adding a new user to the mongo database; Entered user_name is= " + user.getName());
//        try {
//            DBCollection coll = MongoFactory.getCollection(db_name, db_collection);
//
//            // Create a new object and add the new user details to this object.
//            BasicDBObject doc = new BasicDBObject();
//            doc.put("id", String.valueOf(ran.nextInt(100)));
//            doc.put("name", user.getName());
//
//            // Save a new user to the mongo collection.
//            coll.insert(doc);
//            output = true;
//        } catch (Exception e) {
//            output = false;
//            log.error("An error occurred while saving a new user to the mongo database", e);
//        }
//        return output;
//    }
//
//    // Update the selected user in the mongo database.
//    public Boolean edit(User user) {
//        boolean output = false;
//        log.debug("Updating the existing user in the mongo database; Entered user_id is= " + user.getId());
//        try {
//            // Fetching the user details.
//            BasicDBObject existing = (BasicDBObject) getDBObject(user.getId());
//
//            DBCollection coll = MongoFactory.getCollection(db_name, db_collection);
//
//            // Create a new object and assign the updated details.
//            BasicDBObject edited = new BasicDBObject();
//            edited.put("id", user.getId());
//            edited.put("name", user.getName());
//
//            // Update the existing user to the mongo database.
//            coll.update(existing, edited);
//            output = true;
//        } catch (Exception e) {
//            output = false;
//            log.error("An error has occurred while updating an existing user to the mongo database", e);
//        }
//        return output;
//    }
//
//    // Delete a user from the mongo database.
//    public Boolean delete(String id) {
//        boolean output = false;
//        log.debug("Deleting an existing user from the mongo database; Entered user_id is= " + id);
//        try {
//            // Fetching the required user from the mongo database.
//            BasicDBObject item = (BasicDBObject) getDBObject(id);
//
//            DBCollection coll = MongoFactory.getCollection(db_name, db_collection);
//
//            // Deleting the selected user from the mongo database.
//            coll.remove(item);
//            output = true;
//        } catch (Exception e) {
//            output = false;
//            log.error("An error occurred while deleting an existing user from the mongo database", e);
//        }
//        return output;
//    }
//
    // Fetching a particular record from the mongo database.
    private DBObject getDBObject(String id) {
        DBCollection coll = MongoFactory.getCollection(db_name, db_collection);

        // Fetching the record object from the mongo database.
        DBObject where_query = new BasicDBObject();

        // Put the selected user_id to search.
        where_query.put("id", id);
        return coll.findOne(where_query);
    }
//
    // Fetching a single user details from the mongo database.
    public User findUserId(String id) {
        User u = new User();
        DBCollection coll = MongoFactory.getCollection(db_name, db_collection);

        // Fetching the record object from the mongo database.
        DBObject where_query = new BasicDBObject();
        where_query.put("id", id);

        DBObject dbo = coll.findOne(where_query);
        u.setId(dbo.get("id").toString());
        u.setName(dbo.get("name").toString());

        // Return user object.
        return u;
    }
}
