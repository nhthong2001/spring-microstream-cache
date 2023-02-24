package vn.elca.demo.database;

import vn.elca.demo.model.InfoCache;
import vn.elca.demo.model.Params;
import vn.elca.demo.model.ShopAvailabilityData;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Root {

    private final ConcurrentHashMap<Long, ShopAvailabilityData> mapShopAvailabilityData = new ConcurrentHashMap<>();
    private final  ConcurrentHashMap<Params, InfoCache> mapInfoCache = new ConcurrentHashMap<>();
    private final  ConcurrentHashMap<Long, List<Params>> mapInfoData = new ConcurrentHashMap<>();

    public ConcurrentHashMap<Long, ShopAvailabilityData> getMapShopAvailabilityData() {
        return mapShopAvailabilityData;
    }

    public ConcurrentHashMap<Params, InfoCache> getMapInfoCache() {
        return mapInfoCache;
    }

    public ConcurrentHashMap<Long, List<Params>> getMapInfoData() {
        return mapInfoData;
    }
}
