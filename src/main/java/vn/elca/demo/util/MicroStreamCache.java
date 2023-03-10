package vn.elca.demo.util;

import one.microstream.storage.types.StorageManager;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Service;
import vn.elca.demo.database.MicroStreamDatabase;
import vn.elca.demo.database.Root;
import vn.elca.demo.model.AbstractDto;
import vn.elca.demo.model.InfoCache;
import vn.elca.demo.model.Params;
import vn.elca.demo.model.ShopAvailabilityData;
import vn.elca.demo.model.annotation.Cached;
import vn.elca.demo.model.annotation.IgnoreCached;
import vn.elca.demo.model.enumType.DataStructure;
import vn.elca.demo.model.enumType.DataType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MicroStreamCache {
    private final long MAX_MEMORY_CACHE = 536_870_912; // 512MB in bytes
//    private final long STABLE_USE_MEMORY = 15_728_640; // 15MB in bytes
//    private final long MEMORY_USE_FOR_STORED_EACH_OBJECT = 230; // in bytes

    private AtomicLong usageMemoryCache = new AtomicLong(0);

//    private static final StorageManager storageManager = MicroStreamDatabase.getInstance();
    private static final Root root = new Root();

    private final ConcurrentHashMap<String, AbstractDto> mapData = root.getMapData();
    private final ConcurrentHashMap<Params, InfoCache> mapInfoCache = root.getMapInfoCache();
    private final ConcurrentHashMap<String, List<Params>> mapInfoData = root.getMapInfoData();

    public MicroStreamCache() {
//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//
//        Runnable cleanUpRunnable = this::houseKeepingProcess;
//        executor.scheduleAtFixedRate(cleanUpRunnable, 0, 10, TimeUnit.SECONDS);
    }

    private synchronized long getUsageMemoryCache() {
        return usageMemoryCache.get();
    }

    private synchronized void updateUsageMemoryCache() {
        this.usageMemoryCache.set(ObjectSizeCalculator.getObjectSize(root));
    }

//    private long getNumberOfObjectInRoot() {
//        long result = 0;
//        result += mapData.size();
//        result += mapInfoCache.size();
//        result += mapInfoData.size();
//        return result * 2;
//    }

    public void put(Params params, Object value) {
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

        InfoCache infoCache = new InfoCache(dataStructure, dataType);
        mapInfoCache.put(params, infoCache);
//        storageManager.store(mapInfoCache);

        if (dataStructure == DataStructure.SINGLE_OBJECT) {
            AbstractDto temp = (AbstractDto) value;
            this.putDataIntoCache(params, temp, dataType, dataStructure);
            infoCache.getDtoIdMap().put(temp.getId(), null);

        } else if (dataStructure == DataStructure.MAP) {
            Map<Long, AbstractDto> tempMap = (Map) value;
            for (Long key : tempMap.keySet()) {
                this.putDataIntoCache(params, tempMap.get(key), dataType, dataStructure);
                infoCache.getDtoIdMap().put(tempMap.get(key).getId(), key);
            }

        } else {
            for (Object dto : (Collection<?>) value) {
                AbstractDto temp = (AbstractDto) dto;
                this.putDataIntoCache(params, temp, dataType, dataStructure);
                infoCache.getDtoIdMap().put(temp.getId(), null);
            }
        }
//        houseKeepingProcess();
    }

    private void putDataIntoCache(Params params, AbstractDto dto, DataType dataType, DataStructure dataStructure) {
        Long dtoId = dto.getId();
        StringBuilder key = new StringBuilder();
        key.append(dataType.name()).append("-").append(dtoId);
        String dataId = key.toString();

        // Put data into cache and check, put, merge @Cached fields in data
        putDataIntoMapDataAndCheckAnnotation(dataId, dto);

        if (mapInfoData.containsKey(dataId)) {
            if (!mapInfoData.get(dataId).contains(params)) {
                mapInfoData.get(dataId).add(params);
            }
//            storageManager.store(mapInfoData.get(dataId));
        } else {
            List<Params> paramsList = new ArrayList<>();
            paramsList.add(params);
            mapInfoData.put(dataId, paramsList);
//            storageManager.store(mapInfoData);
        }
    }

    private AbstractDto putDataIntoMapDataAndCheckAnnotation(String dataId, AbstractDto dto) {
        AbstractDto data = mergeObjects(mapData.get(dataId), dto);
        if (data == dto) { // is true if dto not existed in mapData
            mapData.put(dataId, data);
//            storageManager.store(mapData);
        } else {
//            storageManager.store(data);
        }
        List<Field> cachedField = FieldUtils.getFieldsListWithAnnotation(data.getClass(), Cached.class);
        List<Field> ignoreField = FieldUtils.getFieldsListWithAnnotation(data.getClass(), IgnoreCached.class);
        if (!cachedField.isEmpty()) {
            cachedField.forEach(field -> {
                if (!ignoreField.contains(field)) {
                    try {
                        field.setAccessible(true);
                        Object temp = field.get(data);
                        if (temp != null) {
                            if (temp instanceof Collection) {
                                Collection<AbstractDto> dataCollection = (Collection<AbstractDto>) temp;

                                Stream<AbstractDto> tempStreamCollectionDto = dataCollection.stream().map(insideDto -> {
                                    String insideDataId = insideDto.getClass().getSimpleName() + "-" + insideDto.getId();
                                    return putDataIntoMapDataAndCheckAnnotation(insideDataId, insideDto);
                                });

                                if (temp instanceof List) {
                                    dataCollection = tempStreamCollectionDto.collect(Collectors.toList());
                                } else if (temp instanceof Set) {
                                    dataCollection = tempStreamCollectionDto.collect(Collectors.toSet());
                                }

                                field.set(dto, dataCollection);
                            } else if (temp instanceof Map) {
                                Map<Object, AbstractDto> mapInsideDto = (Map) temp;
                                for (Object key : mapInsideDto.keySet()) {
                                    AbstractDto insideDto = mapInsideDto.get(key);
                                    String insideDataId = insideDto.getClass().getSimpleName() + "-" + insideDto.getId();
                                    AbstractDto insideData = putDataIntoMapDataAndCheckAnnotation(insideDataId, insideDto);
                                    mapInsideDto.put(key, insideData);
                                }
                                field.set(dto, mapInsideDto);
                            } else { // instance of single object
                                AbstractDto insideDto = (AbstractDto) temp;
                                String insideDataId = insideDto.getClass().getSimpleName() + "-" + insideDto.getId();
                                AbstractDto insideData = putDataIntoMapDataAndCheckAnnotation(insideDataId, insideDto);
                                field.set(dto, insideData);
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    field.setAccessible(true);
                    try {
                        Object temp = field.get(data);
                        if (temp != null) {
                            if (temp instanceof Collection) {
                                Collection<AbstractDto> dataCollection = (Collection<AbstractDto>) temp;
                                dataCollection.stream().forEach(insideDto -> {
                                    String insideDataId = insideDto.getClass().getSimpleName() + "-" + insideDto.getId();
                                    AbstractDto tempData = mergeObjects(mapData.get(insideDataId), insideDto);
                                    if (tempData == insideDto) { // is true if inside not existed in mapData
                                        mapData.put(insideDataId, tempData);
//                                        storageManager.store(mapData);
                                    } else {
//                                        storageManager.store(tempData);
                                    }
                                });

                            } else if (temp instanceof Map) {
                                Map<Object, AbstractDto> mapInsideDto = (Map) temp;
                                for (Object key : mapInsideDto.keySet()) {
                                    AbstractDto insideDto = mapInsideDto.get(key);
                                    String insideDataId = insideDto.getClass().getSimpleName() + "-" + insideDto.getId();
                                    AbstractDto tempData = mergeObjects(mapData.get(insideDataId), insideDto);
                                    if (tempData == insideDto) { // is true if inside not existed in mapData
                                        mapData.put(insideDataId, tempData);
//                                        storageManager.store(mapData);
                                    } else {
//                                        storageManager.store(tempData);
                                    }
                                }
                            } else { // instance of single object
                                AbstractDto insideDto = (AbstractDto) temp;
                                String insideDataId = insideDto.getClass().getSimpleName() + "-" + insideDto.getId();
                                AbstractDto tempData = mergeObjects(mapData.get(insideDataId), insideDto);
                                if (tempData == insideDto) { // is true if dto not existed in mapData
                                    mapData.put(insideDataId, tempData);
//                                    storageManager.store(mapData);
                                } else {
//                                    storageManager.store(tempData);
                                }
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
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
            Set<Long> setDtoId = infoCache.getDtoIdMap().keySet();

            if (dataStructure == DataStructure.SINGLE_OBJECT) {
                return mapData.get(dataType.name() + "-" + setDtoId.iterator().next());
            }

            if (dataStructure == DataStructure.MAP) {
                Map<Long, AbstractDto> mapResult = new HashMap<>();
                Map<Long, Long> dtoIdMap = infoCache.getDtoIdMap();
                setDtoId.forEach(dtoId -> {
                    mapResult.put(dtoIdMap.get(dtoId), mapData.get(dataType.name() + "-" + dtoId));
                });
                return mapResult;
            }

            List<AbstractDto> collectionResult = new ArrayList<>();
            setDtoId.forEach(dtoId -> {
                collectionResult.add(mapData.get(dataType.name() + "-" + dtoId));
            });

            if (dataStructure == DataStructure.SET) {
                return new HashSet<>(collectionResult);
            }

            return collectionResult;
        }

        return null;
    }

    private void houseKeepingProcess() {
        updateUsageMemoryCache();
        System.out.println(getUsageMemoryCache());
        if (getUsageMemoryCache() > MAX_MEMORY_CACHE) {
            List<Params> paramsList = mapInfoCache.entrySet()
                                                  .stream()
                                                  .sorted(Comparator.comparingLong(
                                                          e -> e.getValue().getLastTouched())
                                                  )
                                                  .map(Map.Entry::getKey)
                                                  .collect(Collectors.toList());
            for (Params params : paramsList) {
                InfoCache infoCache = mapInfoCache.get(params);
                Set<Long> listDtoId = infoCache.getDtoIdMap().keySet();
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
//                storageManager.store(mapData);
//                storageManager.store(mapInfoCache);
//                storageManager.store(mapInfoData);

                updateUsageMemoryCache();

                if (getUsageMemoryCache() < MAX_MEMORY_CACHE) {
                    break;
                }
            }
        }
    }
}

