package vn.elca.demo.service;

import com.google.common.collect.Multimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.elca.demo.model.annotation.CustomMicrostreamCached;
import vn.elca.demo.model.CachedAreaBlockAvailability;
import vn.elca.demo.model.Product;
import vn.elca.demo.model.ShopAvailabilityData;
import vn.elca.demo.model.enumType.ShopAvailabilityLevel;

import java.util.*;

@Service
public class DemoService {
//    @Autowired
//    MicroStreamCache cache;

    @Autowired
    ShopAvailabilityDataService shopAvailabilityDataService;

//    public void init(int numberOfObject) {
//        cache.initDB(numberOfObject);
//    }

    @CustomMicrostreamCached
    public ShopAvailabilityData getProductAvailability(Long advantageId, Long composedProductId, long productId) {
        // Xử lí logic lấy ra ShopAvailabilityData
        // Use productId to find ShopAvailabilityData. Define productId = ShopAvailabilityDataId
        ShopAvailabilityData shopAvailabilityData = shopAvailabilityDataService.findShopAvailabilityDataById(productId);
        shopAvailabilityData.setQuota(null);
        shopAvailabilityData.setCompQuota(null);

        return shopAvailabilityData;
    }

    @CustomMicrostreamCached
    public ShopAvailabilityData getProductAvailability(Long advantageId, Long ballotId,
                                                       Long composedProductId, long productId) {
        // Xử lí logic lấy ra ShopAvailabilityData
        ShopAvailabilityData shopAvailabilityData = shopAvailabilityDataService.findShopAvailabilityDataById(productId);

        return shopAvailabilityData;
    }

    @CustomMicrostreamCached
    public Product getProduct(Long productId) {
        // Xử lí logic trả ra product
        Product product = new Product(productId, null, 0, null, null);
        return product;
    }

    @CustomMicrostreamCached
    public Product getProductDetail(Long productId) {
        // Xử lí logic trả ra product
        ShopAvailabilityData shopAvailabilityData = new ShopAvailabilityData(1, ShopAvailabilityLevel.GOOD, 0, null, null);
        Product product = new Product(productId, "Product " + productId, productId, productId * 2,
                                      shopAvailabilityData);
        return product;
    }

    @CustomMicrostreamCached
    public List<Product> getListProduct() {
        List<Product> products = new ArrayList<>();
        for (int i = 50; i <= 150; i++) {
            products.add(new Product(i, "Product " + i, 0, null, null));
        }
        return products;
    }

    @CustomMicrostreamCached
    public Set<ShopAvailabilityData> getSetShopAvailabilityData() {
        Set<ShopAvailabilityData> shopAvailabilityDataHashSet = new HashSet<>();
        for (long i = 1; i < 5; i++) {
            shopAvailabilityDataHashSet.add(shopAvailabilityDataService.findShopAvailabilityDataById(i));
        }
        return shopAvailabilityDataHashSet;
    }

//    @CustomMicrostreamCached
//    public Map<Long, ShopAvailabilityData> getPerformanceAvailability(Long advantageId, Long composedProductId,
//                                                                      long eventId, Collection<Long> performanceIds,
//                                                                      float eventAlertRatio,
//                                                                      Multimap<Long, Long> allowedSeatCategoriesPerPerformance,
//                                                                      Collection<CachedAreaBlockAvailability> preloadedAreaBlockAvailabilities) {
//        Map<Long, ShopAvailabilityData> shopAvailabilityDataHashMap = new HashMap<>();
//        for (long i = 1; i < 5; i++) {
//            shopAvailabilityDataHashMap.put(i, shopAvailabilityDataService.findShopAvailabilityDataById(i));
//        }
//        return shopAvailabilityDataHashMap;
//    }


}
