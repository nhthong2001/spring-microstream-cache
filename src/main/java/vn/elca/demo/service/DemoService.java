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

    @Autowired
    ShopAvailabilityDataService shopAvailabilityDataService;

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
//        ShopAvailabilityData shopAvailabilityData2 = shopAvailabilityDataService.findShopAvailabilityDataById(productId + 1);
//        List<ShopAvailabilityData> list = new ArrayList<>();
//        list.add(shopAvailabilityData);
//        list.add(shopAvailabilityData2);
//
//        Product product = new Product(1, "Product " + 1, 1, 2L, list);
//        shopAvailabilityData.setProduct(product);

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
        ShopAvailabilityData shopAvailabilityData1 = new ShopAvailabilityData(1, ShopAvailabilityLevel.GOOD, 0, null, null);
        ShopAvailabilityData shopAvailabilityData2 = new ShopAvailabilityData(2, ShopAvailabilityLevel.GOOD, 0, null, null);
        ShopAvailabilityData shopAvailabilityData3 = new ShopAvailabilityData(3, ShopAvailabilityLevel.GOOD, 0, null, null);
        ShopAvailabilityData shopAvailabilityData4 = new ShopAvailabilityData(4, ShopAvailabilityLevel.GOOD, 0, null, null);
        List<ShopAvailabilityData> list = new ArrayList<>();
        list.add(shopAvailabilityData1);
        list.add(shopAvailabilityData2);
        list.add(shopAvailabilityData3);
        list.add(shopAvailabilityData4);

        Product product = new Product(productId, "Product " + productId, productId, productId * 2,
                                      list);
        return product;
    }

    @CustomMicrostreamCached
    public List<Product> getListProduct() {
        List<Product> products = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            ShopAvailabilityData shopAvailabilityData = shopAvailabilityDataService.findShopAvailabilityDataById((long) i);
            products.add(new Product(i, "Product " + i, 0, null, Collections.singletonList(shopAvailabilityData)));
        }
        return products;
    }

    @CustomMicrostreamCached
    public Set<ShopAvailabilityData> getSetShopAvailabilityData() {
        Set<ShopAvailabilityData> shopAvailabilityDataHashSet = new HashSet<>();
        for (long i = 1; i < 5; i++) {
            ShopAvailabilityData shopAvailabilityData = new ShopAvailabilityData(i, ShopAvailabilityLevel.GOOD,
                                                                                 0, null, null);
            shopAvailabilityDataHashSet.add(shopAvailabilityData);
        }
        return shopAvailabilityDataHashSet;
    }

    @CustomMicrostreamCached
    public Map<Long, ShopAvailabilityData> getPerformanceAvailability(Long advantageId, Long composedProductId,
                                                                      long eventId, Collection<Long> performanceIds,
                                                                      float eventAlertRatio,
                                                                      Multimap<Long, Long> allowedSeatCategoriesPerPerformance,
                                                                      Collection<CachedAreaBlockAvailability> preloadedAreaBlockAvailabilities) {
        Map<Long, ShopAvailabilityData> shopAvailabilityDataHashMap = new HashMap<>();
        for (long i = 1; i < 5; i++) {
            shopAvailabilityDataHashMap.put(i * 10, shopAvailabilityDataService.findShopAvailabilityDataById(i));
        }
        return shopAvailabilityDataHashMap;
    }


    // Demo

    @CustomMicrostreamCached
    public List<ShopAvailabilityData> getListShopAvailabilityData(long from, long to) {
        List<ShopAvailabilityData> result = new ArrayList<>();
        for (long i = from; i <= to ; i++) {
            result.add(shopAvailabilityDataService.findShopAvailabilityDataById(i));
        }
        return result;
    }


    @CustomMicrostreamCached
    public List<ShopAvailabilityData> getAllShopAvailabilityData() {
        return shopAvailabilityDataService.getAll();
    }

    @CustomMicrostreamCached
    public ShopAvailabilityData getShopAvailabilityDataById(long id) {
        ShopAvailabilityData result = shopAvailabilityDataService.findShopAvailabilityDataById(id);
        return result;
    }
}
