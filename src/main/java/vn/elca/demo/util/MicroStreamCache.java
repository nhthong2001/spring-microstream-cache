package vn.elca.demo.util;

import one.microstream.reference.Lazy;
import one.microstream.reference.LazyReferenceManager;
import one.microstream.storage.types.StorageManager;
import vn.elca.demo.database.DB;
import vn.elca.demo.model.*;

import java.util.*;
import java.util.stream.Stream;

public class MicroStreamCache {

    private long MAX_MEMORY_CACHE = (long) (Runtime.getRuntime().totalMemory() * 0.8);
    private CACHE_MODE MODE = CACHE_MODE.ALWAYS_REFRESH;
    public static final StorageManager storageManager = DB.getInstance();
    public static final Root root = DB.getRoot();

    public MicroStreamCache() {
    }

    public MicroStreamCache(long MAX_MEMORY_CACHE) {
        this.MAX_MEMORY_CACHE = MAX_MEMORY_CACHE;
    }

    public MicroStreamCache(CACHE_MODE MODE) {
        this.MODE = MODE;
    }

    public MicroStreamCache(long MAX_MEMORY_CACHE, CACHE_MODE MODE) {
        this.MAX_MEMORY_CACHE = MAX_MEMORY_CACHE;
        this.MODE = MODE;
    }

    public long getMAX_MEMORY_CACHE() {
        return MAX_MEMORY_CACHE;
    }

    public CACHE_MODE getMODE() {
        return MODE;
    }

    private long getUsedMemory() {
        return MAX_MEMORY_CACHE - Runtime.getRuntime().freeMemory();
    }


    public void put(String id, Object value) {
        List<String> listUserId = new ArrayList<>();
        if (value instanceof Collection) {
            Collection<User> userCollection = (Collection<User>) value;
            userCollection.forEach(user -> {
                if (!root.getMapDto().containsKey(user.getId())) {
                    root.getMapDto().put(user.getId(), new DTO(Lazy.Reference(user)));
                }
            });
            userCollection.stream().map(User::getId).forEach(listUserId::add);
        } else {
            User user = (User) value;
            if (!root.getMapDto().containsKey(user.getId())) {
                root.getMapDto().put(user.getId(), new DTO(Lazy.Reference(user)));
            }
            listUserId.add(user.getId());
        }

        ObjectDTO objectDTO = new ObjectDTO(Lazy.Reference(listUserId));
        if (value instanceof Set) {
            objectDTO.setType(Type.SET);
        } else if (value instanceof List) {
            objectDTO.setType(Type.LIST);
        } else {
            objectDTO.setType(Type.OBJECT);
        }
        root.getMapIdRequestWithObjectDTO().put(id, objectDTO);

        storageManager.store(root.getMapDto());
        storageManager.store(root.getMapIdRequestWithObjectDTO());
    }

    public Object get(String id) {
        ObjectDTO objectDTO = root.getMapIdRequestWithObjectDTO().get(id);
        List<String> ids = objectDTO.getListUserId().get();

        if (objectDTO.getType() == Type.OBJECT) {
            if (isFull()) {
                cleanUp(ids);
            }
            User user = root.getMapDto().get(ids.get(0)).getUser();
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
                result.add(root.getMapDto().get(tempId).getUser());
            });
            Lazy.clear(objectDTO.getListUserId());
            return result;
        } else if (objectDTO.getType() == Type.LIST) {
            result = new ArrayList<>();
            ids.forEach(tempId -> {
                if (isFull()) {
                    cleanUp(ids);
                }
                result.add(root.getMapDto().get(tempId).getUser());
            });
            Lazy.clear(objectDTO.getListUserId());
            return result;
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
        // Stream DTO cần lọc ra các DTO không cần dùng để có dung lượng Ram cho cái cần load lên
        Stream<DTO> dtoStream = root.getMapDto()
                                    .entrySet()
                                    .stream()
                                    .filter(map -> (!listKey.contains(map.getKey())
                                                             && map.getValue().getLazyUser().isLoaded()))
                                    .map(Map.Entry::getValue);

        if (MODE == CACHE_MODE.REFRESH_BY_LAST_ACCESS) {
            dtoStream.min(Comparator.comparing(DTO::getLastTouched)).ifPresent(dto -> {
                Lazy.clear(dto.getLazyUser());
            });
        } else if (MODE == CACHE_MODE.REFRESH_BY_USELESS) {
            dtoStream.min(Comparator.comparing(DTO::getNumberOfUse)).ifPresent(dto -> {
                Lazy.clear(dto.getLazyUser());
            });
        } else {
            cleanUp();
        }
    }


}
