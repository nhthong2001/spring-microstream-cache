package vn.elca.demo.util;

import one.microstream.persistence.types.Storer;
import one.microstream.storage.types.StorageManager;
import org.springframework.stereotype.Service;
import vn.elca.demo.database.MicroStreamDatabase;
import vn.elca.demo.database.Root;
import vn.elca.demo.model.InfoCache;
import vn.elca.demo.model.Params;
import vn.elca.demo.model.ShopAvailabilityData;
import vn.elca.demo.model.enumType.ShopAvailabilityLevel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MicroStreamCache {

    private final long MAX_MEMORY_CACHE = 536870912; // 512MB in bytes
    private final long STABLE_USE_MEMORY = 15728640; // 15MB in bytes
    private final long MEMORY_USE_FOR_STORED_EACH_OBJECT = 230; // 230 bytes

    private AtomicLong usageMemoryCache = new AtomicLong(STABLE_USE_MEMORY);

    private static final StorageManager storageManager = MicroStreamDatabase.getInstance();
    private static final Root root = MicroStreamDatabase.getRoot();

    private final ConcurrentHashMap<Long, ShopAvailabilityData> mapShopAvailabilityData = root.getMapShopAvailabilityData();
    private final ConcurrentHashMap<Params, InfoCache> mapInfoCache = root.getMapInfoCache();
    private final ConcurrentHashMap<Long, List<Params>> mapInfoData = root.getMapInfoData();

    public MicroStreamCache() {
        updateUsageMemoryCache();
    }

    public long getUsageMemoryCache() {
        return usageMemoryCache.get();
    }

    public void updateUsageMemoryCache() {
        this.usageMemoryCache.set(STABLE_USE_MEMORY + ObjectSizeCalculator.getObjectSize(root)
                                  + getNumberOfObjectInRoot() * MEMORY_USE_FOR_STORED_EACH_OBJECT);
    }

    private long getNumberOfObjectInRoot() {
        long result = 0;
        result += mapShopAvailabilityData.size();
        result += mapInfoCache.size();
        result += mapInfoData.size();
        result += mapInfoData.values().stream().mapToInt(List::size).sum();
        return result;
    }

    public void initDB(int numberOfObject) {
        Storer storer = storageManager.createEagerStorer();
        for (int i = 1; i <= numberOfObject; i++) {
            ShopAvailabilityData shopAvailabilityData = new ShopAvailabilityData(i, ShopAvailabilityLevel.GOOD,
                                                                                 1000, 10L, 5L);
            Params params1 = new Params("getProductAvailability(advantageId=null,composedProductId=null,productId=" + i + ")");
            Params params2 = new Params("getProductAvailability(advantageId=null,ballotId=null,composedProductId=null,productId=" + i + ")");

            Long dataId = shopAvailabilityData.getId();

            mapInfoCache.put(params1, new InfoCache(dataId));
            mapInfoCache.put(params2, new InfoCache(dataId));

            mapShopAvailabilityData.put(dataId, shopAvailabilityData);

            List<Params> paramsList = new ArrayList<>();
            paramsList.add(params1);
            paramsList.add(params2);
            mapInfoData.put(dataId, paramsList);
        }
        updateUsageMemoryCache();
        storer.store(root);
        storer.commit();
    }

    // TODO: method used for test only
    public synchronized void put(Params params, ShopAvailabilityData shopAvailabilityData) {
        long neededSize = ObjectSizeCalculator.getObjectSize(shopAvailabilityData)
                          + MEMORY_USE_FOR_STORED_EACH_OBJECT * 6;

        if ((getUsageMemoryCache() + neededSize) > MAX_MEMORY_CACHE) {
            cleanUp(neededSize);
        }
        Long dataId = shopAvailabilityData.getId();

        mapInfoCache.put(params, new InfoCache(dataId));
        storageManager.store(mapInfoCache);

        if (mapShopAvailabilityData.containsKey(dataId)) {
            ShopAvailabilityData data = combineData(mapShopAvailabilityData.get(dataId), shopAvailabilityData);
            storageManager.store(data);
        } else {
            mapShopAvailabilityData.put(dataId, shopAvailabilityData);
            storageManager.store(mapShopAvailabilityData);
        }


        if (mapInfoData.containsKey(dataId)) {
            mapInfoData.get(dataId).add(params);
            storageManager.store(mapInfoData.get(dataId));
        } else {
            List<Params> paramsList = new ArrayList<>();
            paramsList.add(params);
            mapInfoData.put(dataId, paramsList);
            storageManager.store(mapInfoData);
        }

        updateUsageMemoryCache();
    }

    private ShopAvailabilityData combineData(ShopAvailabilityData dataInCache, ShopAvailabilityData dataPut) {
        dataInCache.setLevel(Optional.ofNullable(dataInCache.getLevel())
                                     .orElse(dataPut.getLevel()));

        dataInCache.setQuantity(Math.min(dataInCache.getQuantity(), dataPut.getQuantity()));

        if (dataPut.getQuota() != null) {
            dataInCache.setQuota(Optional.ofNullable(dataInCache.getQuota())
                                         .map(quota -> Math.min(quota, dataPut.getQuota()))
                                         .orElse(dataPut.getQuota()));
        }

        if (dataPut.getCompQuota() != null) {
            dataInCache.setCompQuota(Optional.ofNullable(dataInCache.getCompQuota())
                                             .map(quota -> Math.min(quota, dataPut.getCompQuota()))
                                             .orElse(dataPut.getCompQuota()));
        }

        return dataInCache;
    }

    public synchronized ShopAvailabilityData get(Params params) {
        if (mapInfoCache.containsKey(params)) {
            InfoCache infoCache = mapInfoCache.get(params);
            infoCache.setLastTouched(System.currentTimeMillis());
            Long shopAvailabilityDataId = infoCache.getShopAvailabilityDataId();

            ShopAvailabilityData result = mapShopAvailabilityData.get(shopAvailabilityDataId);

            if (result == null) {
                mapInfoCache.remove(params);
                storageManager.store(mapInfoCache);
            }

            return result;
        }

        return null;
    }

    private void cleanUp(long neededMemory) {
        List<Params> paramsList = mapInfoCache.entrySet()
                                              .stream()
                                              .sorted(Comparator.comparingLong(
                                                      e -> e.getValue().getLastTouched())
                                              )
                                              .map(Map.Entry::getKey)
                                              .collect(Collectors.toList());

        for (Params params : paramsList) {
            Long shopAvailabilityDataId = mapInfoCache.get(params).getShopAvailabilityDataId();

            mapShopAvailabilityData.remove(shopAvailabilityDataId);
            storageManager.store(mapShopAvailabilityData);

            // Delete all key in MapInfoCache have shopAvailabilityData
            for (Params p : mapInfoData.get(shopAvailabilityDataId)) {
                mapInfoCache.remove(p);
            }
            storageManager.store(mapInfoCache);

            mapInfoData.remove(shopAvailabilityDataId);
            storageManager.store(mapInfoData);

            updateUsageMemoryCache();

            if (getUsageMemoryCache() + neededMemory < MAX_MEMORY_CACHE) {
                break;
            }
        }
    }

}
