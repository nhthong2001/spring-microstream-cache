package vn.elca.demo.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.elca.demo.config.MongoFactory;
import vn.elca.demo.model.ShopAvailabilityData;
import vn.elca.demo.model.enumType.ShopAvailabilityLevel;

import java.util.*;

@Service
@Transactional
public class ShopAvailabilityDataService {
    static String db_name = "microstream-cache", db_collection = "test-data";

    // Fetch all shopAvailabilityDatas from the mongo database.
    public List<ShopAvailabilityData> getAll() {
        List<ShopAvailabilityData> shopAvailabilityData_list = new ArrayList<>();
        DBCollection coll = MongoFactory.getCollection(db_name, db_collection);

        // Fetching cursor object for iterating on the database records.
        DBCursor cursor = coll.find();
        while (cursor.hasNext()) {
            DBObject dbo = cursor.next();

            ShopAvailabilityData shopAvailabilityData = new ShopAvailabilityData();
            shopAvailabilityData.setId((Long) dbo.get("id"));
            shopAvailabilityData.setLevel(ShopAvailabilityLevel.valueOf(dbo.get("level").toString()));
            shopAvailabilityData.setQuantity((long) dbo.get("quantity"));
            shopAvailabilityData.setQuota((Long) dbo.get("quota"));
            shopAvailabilityData.setCompQuota((Long) dbo.get("compQuota"));

            // Adding the shopAvailabilityData details to the list.
            shopAvailabilityData_list.add(shopAvailabilityData);
        }
        return shopAvailabilityData_list;
    }


    //    // Add a new shopAvailabilityData to the mongo database.
    public Boolean add(ShopAvailabilityData shopAvailabilityData) {
        boolean output = false;
//        Random ran = new Random();
        try {
            DBCollection coll = MongoFactory.getCollection(db_name, db_collection);

            // Create a new object and add the new shopAvailabilityData details to this object.
            BasicDBObject doc = new BasicDBObject();
            doc.put("id", shopAvailabilityData.getId());
            doc.put("level", shopAvailabilityData.getLevel().toString());
            doc.put("quantity", shopAvailabilityData.getQuantity());
            doc.put("quota", shopAvailabilityData.getQuota());
            doc.put("compQuota", shopAvailabilityData.getCompQuota());

            // Save a new shopAvailabilityData to the mongo collection.
            coll.insert(doc);
            output = true;
        } catch (Exception e) {
            output = false;
        }
        return output;
    }

    // Fetching a single shopAvailabilityData details from the mongo database.
    public ShopAvailabilityData findShopAvailabilityDataById(Long id) {
//        ShopAvailabilityData shopAvailabilityData = new ShopAvailabilityData(id, ShopAvailabilityLevel.GOOD,
//                                                                             1000, 10L, 5L);
        ShopAvailabilityData shopAvailabilityData = new ShopAvailabilityData();
        DBCollection coll = MongoFactory.getCollection(db_name, db_collection);

        // Fetching the record object from the mongo database.
        DBObject where_query = new BasicDBObject();
        where_query.put("id", id);

        DBObject dbo = coll.findOne(where_query);
//        System.out.println("Mongo query: " + where_query);
        shopAvailabilityData.setId((Long) dbo.get("id"));
        shopAvailabilityData.setLevel(ShopAvailabilityLevel.valueOf(dbo.get("level").toString()));
        shopAvailabilityData.setQuantity((long) dbo.get("quantity"));
        shopAvailabilityData.setQuota((Long) dbo.get("quota"));
        shopAvailabilityData.setCompQuota((Long) dbo.get("compQuota"));

//         Return shopAvailabilityData object.
        return shopAvailabilityData;
    }

    public void initData() {
        List<ShopAvailabilityLevel> listLevel = new ArrayList<>();
        listLevel.addAll(Arrays.asList(ShopAvailabilityLevel.GOOD,
                                       ShopAvailabilityLevel.LIMITED, ShopAvailabilityLevel.SOLD_OUT));

        List<Long> quantityList = new ArrayList<>();
        quantityList.addAll(Arrays.asList(100L, 500L, 1000L));

        List<Long> quotaAndCompQuotaList = new ArrayList<>();
        quotaAndCompQuotaList.addAll(Arrays.asList(10L, 15L));


        Random random = new Random();
        for (int i = 101; i <= 1_000_000 ; i++) {
            int levelIndex = random.nextInt(listLevel.size());
            int quantityIndex = random.nextInt(quantityList.size());
            int quotaIndex = random.nextInt(quotaAndCompQuotaList.size());
            int compQuotaIndex = random.nextInt(quotaAndCompQuotaList.size());

            ShopAvailabilityData shopAvailabilityData =
                    new ShopAvailabilityData(i, listLevel.get(levelIndex),
                                             quantityList.get(quantityIndex),
                                             quotaAndCompQuotaList.get(quotaIndex),
                                             quotaAndCompQuotaList.get(compQuotaIndex));

            this.add(shopAvailabilityData);
        }
    }

    //    // Update the selected shopAvailabilityData in the mongo database.
//    public Boolean edit(ShopAvailabilityData shopAvailabilityData) {
//        boolean output = false;
//        log.debug("Updating the existing shopAvailabilityData in the mongo database; Entered shopAvailabilityData_id is= " + shopAvailabilityData.getId());
//        try {
//            // Fetching the shopAvailabilityData details.
//            BasicDBObject existing = (BasicDBObject) getDBObject(shopAvailabilityData.getId());
//
//            DBCollection coll = MongoFactory.getCollection(db_name, db_collection);
//
//            // Create a new object and assign the updated details.
//            BasicDBObject edited = new BasicDBObject();
//            edited.put("id", shopAvailabilityData.getId());
//            edited.put("name", shopAvailabilityData.getName());
//
//            // Update the existing shopAvailabilityData to the mongo database.
//            coll.update(existing, edited);
//            output = true;
//        } catch (Exception e) {
//            output = false;
//            log.error("An error has occurred while updating an existing shopAvailabilityData to the mongo database", e);
//        }
//        return output;
//    }
//
//    // Delete a shopAvailabilityData from the mongo database.
//    public Boolean delete(String id) {
//        boolean output = false;
//        log.debug("Deleting an existing shopAvailabilityData from the mongo database; Entered shopAvailabilityData_id is= " + id);
//        try {
//            // Fetching the required shopAvailabilityData from the mongo database.
//            BasicDBObject item = (BasicDBObject) getDBObject(id);
//
//            DBCollection coll = MongoFactory.getCollection(db_name, db_collection);
//
//            // Deleting the selected shopAvailabilityData from the mongo database.
//            coll.remove(item);
//            output = true;
//        } catch (Exception e) {
//            output = false;
//            log.error("An error occurred while deleting an existing shopAvailabilityData from the mongo database", e);
//        }
//        return output;
//    }
//
    // Fetching a particular record from the mongo database.
//    private DBObject getDBObject(String id) {
//        DBCollection coll = MongoFactory.getCollection(db_name, db_collection);
//
//        // Fetching the record object from the mongo database.
//        DBObject where_query = new BasicDBObject();
//
//        // Put the selected shopAvailabilityData_id to search.
//        where_query.put("id", id);
//        return coll.findOne(where_query);
//    }


}
