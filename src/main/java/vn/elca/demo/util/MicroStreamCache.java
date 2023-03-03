package vn.elca.demo.util;

import one.microstream.persistence.types.Storer;
import one.microstream.storage.types.StorageManager;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Service;
import vn.elca.demo.database.MicroStreamDatabase;
import vn.elca.demo.database.Root;
import vn.elca.demo.model.*;
import vn.elca.demo.model.annotation.Check;
import vn.elca.demo.model.enumType.DataStructure;
import vn.elca.demo.model.enumType.DataType;
import vn.elca.demo.model.enumType.ShopAvailabilityLevel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MicroStreamCache {

    private final long MAX_MEMORY_CACHE = 504_000_000;//536870912; // 512MB in bytes
    private final long STABLE_USE_MEMORY = 15_728_640; // 15MB in bytes
    private final long MEMORY_USE_FOR_STORED_EACH_OBJECT = 230; // in bytes

    private AtomicLong usageMemoryCache = new AtomicLong(STABLE_USE_MEMORY);

    private static final StorageManager storageManager = MicroStreamDatabase.getInstance();
    private static final Root root = MicroStreamDatabase.getRoot();

    private final ConcurrentHashMap<String, AbstractDto> mapData = root.getMapData();
    private final ConcurrentHashMap<Params, InfoCache> mapInfoCache = root.getMapInfoCache();
    private final ConcurrentHashMap<String, List<Params>> mapInfoData = root.getMapInfoData();

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
        result += mapData.size();
        result += mapInfoCache.size();
        result += mapInfoData.size();
        return result * 2;
    }

    // TODO: method used for test only
    public synchronized void put(Params params, Object value) {
        DataStructure dataStructure;
        DataType dataType = null;

        if (value instanceof List) {
            dataStructure = DataStructure.LIST;
            if (!((List<?>) value).isEmpty()) {
                dataType = DataType.valueOf(((List<?>) value).get(0).getClass().getSimpleName());
            }
        } else if (value instanceof Set) {
            dataStructure = DataStructure.SET;
            if (!((Set<?>) value).isEmpty()) {
                dataType = DataType.valueOf(((Set<?>) value).iterator().next().getClass().getSimpleName());
            }
        } else if (value instanceof Map) {
            dataStructure = DataStructure.MAP;
            if (!((Map<?, ?>) value).isEmpty()) {
                for (Object dto : ((Map<?, ?>) value).values()) {
                    dataType = DataType.valueOf(dto.getClass().getSimpleName());
                    break;
                }
            }
        } else {
            dataStructure = DataStructure.SINGLE_OBJECT;
            dataType = DataType.valueOf(value.getClass().getSimpleName());
        }


        if (dataStructure == DataStructure.SINGLE_OBJECT) {
            this.putObject(params, (AbstractDto) value, dataType, dataStructure);
        } else if (dataStructure == DataStructure.MAP) {
            for (Object dto : ((Map<?, ?>) value).values()) {
                this.putObject(params, (AbstractDto) dto, dataType, dataStructure);
            }
        } else {
            for (Object dto : (Collection<?>) value) {
                this.putObject(params, (AbstractDto) dto, dataType, dataStructure);
            }
        }

//        updateUsageMemoryCache();
//
//        if (getUsageMemoryCache() > MAX_MEMORY_CACHE) {
//            cleanUp();
//        }
    }

    private void putObject(Params params, AbstractDto dto, DataType dataType, DataStructure dataStructure) {
        Long dtoId = dto.getId();
        StringBuilder key = new StringBuilder();
        key.append(dataType.name()).append("-").append(dtoId);
        String dataId = key.toString();

        InfoCache infoCache;
        if (mapInfoCache.containsKey(params)) {
            infoCache = mapInfoCache.get(params);
            if (!infoCache.getListDtoId().contains(dtoId)) {
                infoCache.getListDtoId().add(dtoId);
            }
            storageManager.store(infoCache);
        } else {
            infoCache = new InfoCache(dataStructure, dataType);
            infoCache.getListDtoId().add(dtoId);
            mapInfoCache.put(params, infoCache);
            storageManager.store(mapInfoCache);
        }

        // Put data into cache and check, put, merge @Check fields in data
        putDataInToMapData(dataId, dto);

        if (mapInfoData.containsKey(dataId)) {
            if (!mapInfoData.get(dataId).contains(params)) {
                mapInfoData.get(dataId).add(params);
            }
            storageManager.store(mapInfoData.get(dataId));
        } else {
            List<Params> paramsList = new ArrayList<>();
            paramsList.add(params);
            mapInfoData.put(dataId, paramsList);
            storageManager.store(mapInfoData);
        }
    }

    private AbstractDto putDataInToMapData(String dataId, AbstractDto dto) {
        AbstractDto data = mergeObjects(mapData.get(dataId), dto);
        mapData.put(dataId, data);
        storageManager.store(data);
        if (data != dto) {
            storageManager.store(mapData);
        }
        List<Field> annotationField = FieldUtils.getFieldsListWithAnnotation(data.getClass(), Check.class);
        annotationField.forEach(field -> {
            try {
                field.setAccessible(true);
                AbstractDto insideDto = (AbstractDto) field.get(data);
                String insideDataId = insideDto.getClass().getSimpleName() + "-" + insideDto.getId();

                if (mapData.containsKey(insideDataId)) {
                    // merge data between @Check field existed in cache and field in data putting into cache
                    AbstractDto insideData = putDataInToMapData(insideDataId, insideDto);

                    // set field @Check in data equal insideData
                    List<Field> dataFields = FieldUtils.getAllFieldsList(dto.getClass());
                    Field changeField = dataFields.get(dataFields.indexOf(field));
                    changeField.setAccessible(true);
                    changeField.set(dto, insideData);
                    storageManager.store(insideData);

                } else {
                    mapData.put(insideDataId, insideDto);
                    storageManager.store(mapData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return data;
    }

    private <T> T mergeObjects(T des, T src) {
        if (des == null) {
            return (T) src;
        }

        List<Field> fields = FieldUtils.getAllFieldsList(des.getClass())
                                       .stream()
                                       .filter(field -> !Modifier.isStatic(field.getModifiers()))
                                       .collect(Collectors.toList());

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value1 = field.get(des);
                Object value2 = field.get(src);
                Object value = (value1 != null) ? value1 : value2;

                if (des instanceof ShopAvailabilityData) {
                    value = getValueForShopAvailabilityDataObject(field, value1, value2, value);
                }
                field.set(des, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) des;
    }

    private Object getValueForShopAvailabilityDataObject(Field field, Object value1, Object value2, Object value) {
        if ("quantity".equals(field.getName())) {
            value = Math.min((long) value1, (long) value2);
        }

        if (value1 != null && value2 != null) {
            if ("quota".equals(field.getName()) || "comQuota".equals(field.getName())) {
                value = Math.min((Long) value1, (Long) value2);
            }
        }
        return value;
    }

    public synchronized Object get(Params params) {
        if (mapInfoCache.containsKey(params)) {
            InfoCache infoCache = mapInfoCache.get(params);
            infoCache.setLastTouched(System.currentTimeMillis());

            DataStructure dataStructure = infoCache.getDataStructure();
            DataType dataType = infoCache.getDataType();
            List<Long> listDtoId = infoCache.getListDtoId();

            if (dataStructure == DataStructure.SINGLE_OBJECT) {
                return mapData.get(dataType.name() + "-" + listDtoId.get(0));
            }

            if (dataStructure == DataStructure.MAP) {
                Map<Long, AbstractDto> mapResult = new HashMap<>();
                listDtoId.forEach(dtoId -> {
                    mapResult.put(dtoId, mapData.get(dataType.name() + "-" + dtoId));
                });
                return mapResult;
            }

            List<AbstractDto> collectionResult = new ArrayList<>();
            listDtoId.forEach(dtoId -> {
                collectionResult.add(mapData.get(dataType.name() + "-" + dtoId));
            });

            if (dataStructure == DataStructure.SET) {
                return new HashSet<>(collectionResult);
            }

            return collectionResult;
        }

        return null;
    }

    private void cleanUp() {
        List<Params> paramsList = mapInfoCache.entrySet()
                                              .stream()
                                              .sorted(Comparator.comparingLong(
                                                      e -> e.getValue().getLastTouched())
                                              )
                                              .map(Map.Entry::getKey)
                                              .collect(Collectors.toList());

        for (Params params : paramsList) {
            InfoCache infoCache = mapInfoCache.get(params);
            List<Long> listDtoId = infoCache.getListDtoId();
            DataType dataType = infoCache.getDataType();

            listDtoId.forEach(dtoId -> {
                String dataId = dataType.name() + "-" + dtoId;
                mapData.remove(dataId);

                // Delete all key in MapInfoCache have AbstractDto
                for (Params p : mapInfoData.get(dataId)) {
                    mapInfoCache.remove(p);
                }

                mapInfoData.remove(dataId);
            });
            storageManager.store(mapData);
            storageManager.store(mapInfoCache);
            storageManager.store(mapInfoData);

            updateUsageMemoryCache();

            if (getUsageMemoryCache() < MAX_MEMORY_CACHE) {
                break;
            }
        }
    }

    public void initDB(int numberOfObject) {
        Storer storer = storageManager.createEagerStorer();
        for (int i = 1; i <= numberOfObject; i++) {
            ShopAvailabilityData shopAvailabilityData = new ShopAvailabilityData(i, ShopAvailabilityLevel.GOOD,
                                                                                 1000, 10L, 5L);
            Params params1 = new Params("getProductAvailability(advantageId=null,composedProductId=null,productId=" + i + ")");
            Params params2 = new Params("getProductAvailability(advantageId=null,ballotId=null,composedProductId=null,productId=" + i + ")");

            Long dtoId = shopAvailabilityData.getId();

            InfoCache infoCache1 = new InfoCache(DataStructure.SINGLE_OBJECT, DataType.ShopAvailabilityData);
            infoCache1.getListDtoId().add(dtoId);
            mapInfoCache.put(params1, infoCache1);

            InfoCache infoCache2 = new InfoCache(DataStructure.SINGLE_OBJECT, DataType.ShopAvailabilityData);
            infoCache2.getListDtoId().add(dtoId);
            mapInfoCache.put(params2, infoCache2);


            mapData.put("ShopAvailabilityData-" + dtoId, shopAvailabilityData);

            List<Params> paramsList = new ArrayList<>();
            paramsList.add(params1);
            paramsList.add(params2);
            mapInfoData.put("ShopAvailabilityData-" + dtoId, paramsList);
        }
        storer.store(root);
        storer.commit();
        updateUsageMemoryCache();
    }
}

