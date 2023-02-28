package vn.elca.demo.service;

import com.google.common.collect.Multimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.elca.demo.aop.CustomMicrostreamCached;
import vn.elca.demo.model.Params;
import vn.elca.demo.model.ShopAvailabilityData;
import vn.elca.demo.util.MicroStreamCache;

import java.util.Collection;
import java.util.Map;

@Service
public class CacheService {
    @Autowired
    MicroStreamCache cache;

    @Autowired
    ShopAvailabilityDataService shopAvailabilityDataService;

    public void init(int numberOfObject) {
        cache.initDB(numberOfObject);
    }

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



//    public Map<Long, ShopAvailabilityData> getPerformanceAvailability(Long advantageId, Long composedProductId,
//                                                                      long eventId, Collection<Long> performanceIds,
//                                                                      float eventAlertRatio,
//                                                                      Multimap<Long, Long> allowedSeatCategoriesPerPerformance
//            final Collection<CachedAreaBlockAvailability> preloadedAreaBlockAvailabilities) {
//
//        return null;
//    }
}
