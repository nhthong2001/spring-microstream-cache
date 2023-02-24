package vn.elca.demo.service;

import com.google.common.collect.Multimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public ShopAvailabilityData getProductAvailability(Long advantageId, Long composedProductId, long productId) {
        Params params = new Params("getProductAvailability: advantageId=" + advantageId + "composedProductId="
                                   + composedProductId + "productId=" + productId);
        if (cache.get(params) != null) {
            System.out.println("Get from cache");
            return cache.get(params);
        }

        // Xử lí logic lấy ra ShopAvailabilityData
        // Use productId to find ShopAvailabilityData. Define productId = ShopAvailabilityDataId
        ShopAvailabilityData shopAvailabilityData = shopAvailabilityDataService.findShopAvailabilityDataById(productId);
        shopAvailabilityData.setQuota(null);
        shopAvailabilityData.setCompQuota(null);


        // Cache shopAvailabilityData
        cache.put(params, shopAvailabilityData);

        return cache.get(params);
    }

    public ShopAvailabilityData getProductAvailability(Long advantageId, Long ballotId,
                                                       Long composedProductId, long productId) {
        Params params = new Params("getProductAvailability: advantageId=" + advantageId + "ballotId=" + ballotId
                                   + "composedProductId=" + composedProductId + "productId=" + productId);

        if (cache.get(params) != null) {
            System.out.println("Get from cache");
            return cache.get(params);
        }

        // Xử lí logic lấy ra ShopAvailabilityData
        ShopAvailabilityData shopAvailabilityData = shopAvailabilityDataService.findShopAvailabilityDataById(productId);
        // Cache shopAvailabilityData
        cache.put(params, shopAvailabilityData);

        return shopAvailabilityData;
    }

    public Map<Long, ShopAvailabilityData> getPerformanceAvailability(Long advantageId, Long composedProductId,
                                                                      long eventId, Collection<Long> performanceIds,
                                                                      float eventAlertRatio,
                                                                      Multimap<Long, Long> allowedSeatCategoriesPerPerformance
            /*final Collection<CachedAreaBlockAvailability> preloadedAreaBlockAvailabilities*/) {


        return null;
    }
}
