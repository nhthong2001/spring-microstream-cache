package vn.elca.demo.util;

import one.microstream.storage.types.StorageManager;
import org.springframework.stereotype.Service;
import vn.elca.demo.database.MicroStreamDatabase;
import vn.elca.demo.database.Root;
import vn.elca.demo.model.InfoCache;
import vn.elca.demo.model.Params;
import vn.elca.demo.model.ShopAvailabilityData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MicroStreamCache {

    private final long MAX_MEMORY_CACHE = 536870912; // 512MB in bytes
    private final long STABLE_USE_MEMORY = 15728640; // 15MB in bytes

    private AtomicLong usageMemoryCache = new AtomicLong(STABLE_USE_MEMORY);

    private static final StorageManager storageManager = MicroStreamDatabase.getInstance();
    private static final Root root = MicroStreamDatabase.getRoot();

    private final ConcurrentHashMap<Long, ShopAvailabilityData> mapShopAvailabilityData = root.getMapShopAvailabilityData();
    private final ConcurrentHashMap<Params, InfoCache> mapInfoCache = root.getMapInfoCache();
    private final ConcurrentHashMap<Long, List<Params>> mapInfoData = root.getMapInfoData();

    public MicroStreamCache() {
    }

    public long getUsageMemoryCache() {
        return usageMemoryCache.get();
    }

    public void updateUsageMemoryCache() {
        this.usageMemoryCache.set(STABLE_USE_MEMORY + ObjectSizeCalculator.getObjectSize(root));
    }

    // TODO: method used for test only
    public synchronized void put(Params params, ShopAvailabilityData shopAvailabilityData) {
        long neededSize = ObjectSizeCalculator.getObjectSize(shopAvailabilityData);
        if ((getUsageMemoryCache() + neededSize) > MAX_MEMORY_CACHE) {
            cleanUp(neededSize);
        }

        Long dataId = shopAvailabilityData.getId();

        mapInfoCache.put(params, new InfoCache(dataId));
        storageManager.store(mapInfoCache);


        ShopAvailabilityData data = combineData(mapShopAvailabilityData.get(dataId), shopAvailabilityData);
        storageManager.store(data);

        mapShopAvailabilityData.put(dataId, data);
        storageManager.store(mapShopAvailabilityData);

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
        if (dataInCache == null) {
            return dataPut;
        }
        ShopAvailabilityData result = new ShopAvailabilityData();

//        result.setId(dataInCache.getId());
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
            ;

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


            long usageMemoryBeforeCleanUp = getUsageMemoryCache();
            updateUsageMemoryCache();

            if ((usageMemoryBeforeCleanUp - getUsageMemoryCache()) >= neededMemory) {
                break;
            }
        }

    }

}
