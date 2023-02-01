package vn.elca.demo.util;

import one.microstream.persistence.types.PersistenceManager;
import one.microstream.persistence.types.PersistenceObjectManager;
import one.microstream.persistence.types.PersistenceStoring;
import one.microstream.reference.Lazy;
import one.microstream.reference.LazyReferenceManager;
import one.microstream.storage.types.StorageManager;
import org.springframework.data.annotation.Persistent;
import vn.elca.demo.database.MicroStreamDatabase;
import vn.elca.demo.model.*;

import java.util.*;
import java.util.stream.Stream;

public class MicroStreamCache {

    private long MAX_MEMORY_CACHE = (long) (Runtime.getRuntime().totalMemory() * 0.8);
    private CacheMode MODE = CacheMode.ALWAYS_REFRESH;

    public static final StorageManager storageManager = MicroStreamDatabase.getInstance();
    public static final Root root = MicroStreamDatabase.getRoot();

    private final Map<String, Dto> mapDto = root.getMapDto();
    private final Map<String, CacheDto> mapIdRequestWithCacheDto = root.getMapIdRequestWithCacheDto();

    public MicroStreamCache() {
    }

    public MicroStreamCache(long MAX_MEMORY_CACHE) {
        this.MAX_MEMORY_CACHE = MAX_MEMORY_CACHE;
    }

    public MicroStreamCache(CacheMode MODE) {
        this.MODE = MODE;
    }

    public MicroStreamCache(long MAX_MEMORY_CACHE, CacheMode MODE) {
        this.MAX_MEMORY_CACHE = MAX_MEMORY_CACHE;
        this.MODE = MODE;
    }

    public long getMAX_MEMORY_CACHE() {
        return MAX_MEMORY_CACHE;
    }

    public CacheMode getMODE() {
        return MODE;
    }

    private long getUsedMemory() {
        return MAX_MEMORY_CACHE - Runtime.getRuntime().freeMemory();
    }


    public void put(String id, Object value) {
        List<String> listUserId = new ArrayList<>();
        if (!mapIdRequestWithCacheDto.containsKey(id)) {
            if (value instanceof Collection) {
                Collection<User> userCollection = (Collection<User>) value;
                userCollection.forEach(user -> {
                    if (!mapDto.containsKey(user.getId())) {
                        Dto dto = new Dto();
                        dto.setUser(user);
                        mapDto.put(user.getId(), dto);
                        storageManager.store(mapDto);
                        Lazy.clear(dto.getLazyUser());
                    }
                });
                userCollection.stream().map(User::getId).forEach(listUserId::add);
            } else {
                User user = (User) value;
                if (!mapDto.containsKey(user.getId())) {
                    Dto dto = new Dto();
                    dto.setUser(user);
                    mapDto.put(user.getId(), dto);
                    long objectId = storageManager.store(mapDto);

                    Lazy.clear(dto.getLazyUser());
                }
                listUserId.add(user.getId());
            }
            CacheDto cacheDto = new CacheDto(Lazy.Reference(listUserId));
            if (value instanceof Set) {
                cacheDto.setType(Type.SET);
            } else if (value instanceof List) {
                cacheDto.setType(Type.LIST);
            } else {
                cacheDto.setType(Type.OBJECT);
            }
            mapIdRequestWithCacheDto.put(id, cacheDto);

            storageManager.store(mapIdRequestWithCacheDto);
            Lazy.clear(cacheDto.getListUserId());
        }
    }

    public Object get(String id) {
        CacheDto objectDTO = mapIdRequestWithCacheDto.get(id);
        List<String> ids = objectDTO.getListUserId().get();
        if (MODE == CacheMode.ALWAYS_REFRESH) {
            if (objectDTO.getType() == Type.OBJECT) {
                User user = mapDto.get(ids.get(0)).getUser();
                cleanUp();
                return user;
            }
            Collection<User> result;
            if (objectDTO.getType() == Type.SET) {
                result = new HashSet<>();
                ids.forEach(tempId -> {
                    result.add(mapDto.get(tempId).getUser());
                });
                cleanUp();
                return result;
            } else if (objectDTO.getType() == Type.LIST) {
                result = new ArrayList<>();
                ids.forEach(tempId -> {
                    result.add(mapDto.get(tempId).getUser());
                });
                cleanUp();
                return result;
            }
        } else {
            if (objectDTO.getType() == Type.OBJECT) {
                if (isFull()) {
                    cleanUp(ids);
                }
                User user = mapDto.get(ids.get(0)).getUser();
                Lazy.clear(objectDTO.getListUserId());
                return user;
            }
            Collection<User> result;
            if (objectDTO.getType() == Type.SET) {
                result = new HashSet<>();
                ids.forEach(tempId -> {
                    if (isFull()) {
                        cleanUp(ids);
                    }
                    result.add(mapDto.get(tempId).getUser());
                });
                Lazy.clear(objectDTO.getListUserId());
                return result;
            } else if (objectDTO.getType() == Type.LIST) {
                result = new ArrayList<>();
                ids.forEach(tempId -> {
                    if (isFull()) {
                        cleanUp(ids);
                    }
                    result.add(mapDto.get(tempId).getUser());
                });
                Lazy.clear(objectDTO.getListUserId());
                return result;
            }
        }

        return null;
    }

    private boolean isFull() {
        return getUsedMemory() >= MAX_MEMORY_CACHE;
    }

    public void cleanUp() {
        LazyReferenceManager.get().iterate(lazyRef -> {
            if (lazyRef.isLoaded()) {
                lazyRef.clear();
            }
        });
    }

    public void cleanUp(List<String> listKey) {
        // Stream DTO dùng để lọc ra các DTO không có trong list key cần load
        Stream<Dto> dtoStream = mapDto
                .entrySet()
                .stream()
                .filter(map -> (!listKey.contains(map.getKey())
                                && map.getValue().getLazyUser().isLoaded()))
                .map(Map.Entry::getValue);

        if (MODE == CacheMode.REFRESH_BY_LAST_ACCESS) {
            dtoStream.min(Comparator.comparing(Dto::getLastTouched))
                     .ifPresent(dto -> {
                         Lazy.clear(dto.getLazyUser());
                     });
        } else if (MODE == CacheMode.REFRESH_BY_USELESS) {
            dtoStream.min(Comparator.comparing(Dto::getNumberOfUse))
                     .ifPresent(dto -> {
                         Lazy.clear(dto.getLazyUser());
                     });
        }
    }


}
