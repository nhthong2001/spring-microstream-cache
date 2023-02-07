package vn.elca.demo.util;

import one.microstream.storage.types.StorageManager;
import org.springframework.stereotype.Service;
import vn.elca.demo.database.MicroStreamDatabase;
import vn.elca.demo.database.Root;
import vn.elca.demo.model.Dto;
import vn.elca.demo.model.InfoCache;
import vn.elca.demo.model.InfoDto;
import vn.elca.demo.model.Type;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MicroStreamCache {

    private final long MAX_MEMORY_CACHE = 536870912; // 512MB
    private final long STABLE_USE_MEMORY = 15728640; // 15MB
    private AtomicLong usageMemoryCache = new AtomicLong(STABLE_USE_MEMORY);

    private static final StorageManager storageManager = MicroStreamDatabase.getInstance();
    private static final Root root = MicroStreamDatabase.getRoot();

    private final Map<String, Dto> mapDto = root.getMapDto();
    private final Map<String, Set<Dto>> mapSetDto = root.getMapSetDto();
    private final Map<String, List<Dto>> mapListDto = root.getMapListDto();
    private final Map<String, InfoCache> mapInfoCache = root.getMapInfoCache();
    private final Map<String, InfoDto> mapInfoDto = root.getMapInfoDto();

    public MicroStreamCache() {
    }

    public long getUsageMemoryCache() {
        return usageMemoryCache.get();
    }

    public void updateUsageMemoryCache() {

        long beforeMemory = Runtime.getRuntime().freeMemory();
        long start = System.currentTimeMillis();

        this.usageMemoryCache.set(STABLE_USE_MEMORY + ObjectSizeCalculator.getObjectSize(root));

        long afterMemory = Runtime.getRuntime().freeMemory();
        long end = System.currentTimeMillis();

        System.out.println("Time to update usage memory: " + (end - start) + "ms");
        System.out.println("Memory used to update usage memory: " + (beforeMemory - afterMemory) + " bytes");
    }

    public void put(String cacheId, Object value) {
        if (!mapInfoCache.containsKey(cacheId)) {

            long neededMemory = ObjectSizeCalculator.getObjectSize(value);
            if (value instanceof Dto) {
                Dto dto = (Dto) value;
                if (mapInfoDto.containsKey(dto.getId())) {
                    neededMemory = 0;
                }
            } else {
                Collection<Dto> collection = (Collection<Dto>) value;

                final long[] availableMemory = {0};
                collection.stream().map(Dto::getId).forEach(dtoId -> {
                    if (mapInfoDto.containsKey(dtoId)) {
                        availableMemory[0] += ObjectSizeCalculator.getObjectSize(mapInfoDto.get(dtoId).getDto());
                    }
                });

                if ((getUsageMemoryCache() + neededMemory - availableMemory[0]) >= MAX_MEMORY_CACHE) {
                    cleanUp(neededMemory);
                }
            }

            if (value instanceof Set) {
                mapInfoCache.put(cacheId, new InfoCache(Type.SET));

                Set<Dto> setDto = (Set<Dto>) value;
                setDto = setDto.stream().map(dto -> findReference(dto, cacheId)).collect(Collectors.toSet());


                mapSetDto.put(cacheId, setDto);
                storageManager.store(mapSetDto);

            } else if (value instanceof List) {
                mapInfoCache.put(cacheId, new InfoCache(Type.LIST));

                List<Dto> listDto = (List<Dto>) value;

                for (int i = 0; i < listDto.size(); i++) {
                    Dto tempDto = findReference(listDto.get(i), cacheId);

                    if (tempDto != listDto.get(i)) {
                        listDto.set(i, tempDto);
                    }
                }

                mapListDto.put(cacheId, listDto);
                storageManager.store(mapListDto);

            } else {
                mapInfoCache.put(cacheId, new InfoCache(Type.OBJECT));
                Dto dto = (Dto) value;

                dto = findReference(dto, cacheId);

                mapDto.put(cacheId, dto);
                storageManager.store(mapDto);
            }
            storageManager.store(mapInfoCache);

            updateUsageMemoryCache();
        }
    }

    private Dto findReference(Dto dto, String cacheId) {
        String dtoId = dto.getId();

        if (mapInfoDto.containsKey(dtoId)) {
            InfoDto infoDto = mapInfoDto.get(dtoId);
            infoDto.getListCacheId().add(cacheId);

            storageManager.store(infoDto);
            storageManager.store(mapInfoDto);

            return infoDto.getDto();
        }

        InfoDto infoDto = new InfoDto();
        infoDto.setDto(dto);
        infoDto.getListCacheId().add(cacheId);

        mapInfoDto.put(dtoId, infoDto);

        storageManager.store(infoDto);
        storageManager.store(mapInfoDto);

        return dto;
    }

    public Object get(String id) {
        if (mapInfoCache.containsKey(id)) {
            InfoCache info = mapInfoCache.get(id);

            info.setLastTouched(System.currentTimeMillis());
            storageManager.store(info);


            if (info.getType() == Type.LIST) {
                return mapListDto.get(id);

            } else if (info.getType() == Type.SET) {
                return mapSetDto.get(id);

            } else {
                return mapDto.get(id);
            }
        }
        return null;
    }

    public void cleanUp(long neededMemory) {
        List<String> listCacheId = mapInfoCache.entrySet()
                                               .stream()
                                               .sorted(Comparator.comparingLong(
                                                       e -> e.getValue().getLastTouched())
                                               )
                                               .map(Map.Entry::getKey)
                                               .collect(Collectors.toList());

        for (String cacheId : listCacheId) {
            InfoCache infoCache = mapInfoCache.get(cacheId);

            if (infoCache.getType() == Type.SET) {
                mapSetDto.get(cacheId)
                         .stream()
                         .map(Dto::getId)
                         .forEach(dtoId -> {
                             InfoDto infoDto = mapInfoDto.get(dtoId);
                             infoDto.getListCacheId().remove(cacheId);
                             if (infoDto.getListCacheId().size() == 0) {
                                 mapInfoDto.remove(dtoId);
                                 storageManager.store(mapInfoDto);
                             }
                         });

                mapSetDto.remove(cacheId);
                storageManager.store(mapSetDto);

            } else if (infoCache.getType() == Type.LIST) {
                mapListDto.get(cacheId)
                          .stream()
                          .map(Dto::getId)
                          .forEach(dtoId -> {
                              InfoDto infoDto = mapInfoDto.get(dtoId);
                              infoDto.getListCacheId().remove(cacheId);
                              if (infoDto.getListCacheId().size() == 0) {
                                  mapInfoDto.remove(dtoId);
                                  storageManager.store(mapInfoDto);
                              }
                          });

                mapListDto.remove(cacheId);
                storageManager.store(mapListDto);

            } else {
                String dtoId = mapDto.get(cacheId).getId();
                InfoDto infoDto = mapInfoDto.get(dtoId);

                infoDto.getListCacheId().remove(cacheId);
                if (infoDto.getListCacheId().size() == 0) {
                    mapInfoDto.remove(dtoId);
                    storageManager.store(mapInfoDto);
                }

                mapDto.remove(cacheId);
                storageManager.store(mapDto);
            }

            mapInfoCache.remove(cacheId);
            storageManager.store(mapInfoCache);


            long usageMemoryBeforeCleanUp = getUsageMemoryCache();
            updateUsageMemoryCache();

            if ((usageMemoryBeforeCleanUp - getUsageMemoryCache()) >= neededMemory) {
                break;
            }
        }

    }
}
