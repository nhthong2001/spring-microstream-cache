package vn.elca.demo.util;

import one.microstream.reference.Lazy;
import one.microstream.reference.LazyReferenceManager;
import one.microstream.storage.types.StorageManager;
import vn.elca.demo.database.DB;
import vn.elca.demo.model.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MicroStreamCache {

    private long MAX_MEMORY_CACHE = Runtime.getRuntime().totalMemory();
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
        if (isFull(ids)) {
            cleanUp(ids);
        }
        if (objectDTO.getType() == Type.OBJECT) {
            User user = root.getMapDto().get(ids.get(0)).getUser();
            Lazy.clear(objectDTO.getListUserId());
            return user;
        }
        Collection<User> result;
        if (objectDTO.getType() == Type.SET) {
            result = new HashSet<>();
            ids.forEach(tempId -> {
                result.add(root.getMapDto().get(tempId).getUser());
            });
            Lazy.clear(objectDTO.getListUserId());
            return result;
        } else if (objectDTO.getType() == Type.LIST) {
            result = new ArrayList<>();
            ids.forEach(tempId -> {
                result.add(root.getMapDto().get(tempId).getUser());
            });
            Lazy.clear(objectDTO.getListUserId());
            return result;
        }

        return null;
    }

    private List<String> findListKeyNotLoaded(List<String> list) {
        List<String> listRefLoadedInList = root.getMapDto().entrySet().stream()
                                               .filter(map -> map.getValue().getLazyUser().isLoaded())
                                               .filter(map -> list.contains(map.getKey()))
                                               .map(Map.Entry::getValue)
                                               .map(value -> value.getUser().getId())
                                               .collect(Collectors.toList());
        return list.stream().filter(s -> !listRefLoadedInList.contains(s)).collect(Collectors.toList());
    }


    private boolean isFull(List<String> list) {
        boolean result = false;
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        if (usedMemory >= (MAX_MEMORY_CACHE)) {
            return true;
        }

        long loadedRef = root.getMapDto().values().stream().filter(dto -> dto.getLazyUser().isLoaded()).count();
        if (loadedRef == 0) {
            return false;
        }
        ;
        List<String> listKeyNotLoaded = findListKeyNotLoaded(list);
        if (listKeyNotLoaded.size() > 0) {
            root.getMapDto().get(listKeyNotLoaded.get(0)).getLazyUser().get();

            long usedMemoryAfterGetOne = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long memoryForEachDTO = usedMemoryAfterGetOne - usedMemory;
            long refNeedLoad = listKeyNotLoaded.size();
            long neededMemory = refNeedLoad * memoryForEachDTO;
            if (usedMemory + neededMemory >= MAX_MEMORY_CACHE) {
                return true;
            }
        }
        return result;
    }

    public void cleanUp() {
        LazyReferenceManager.get().iterate(lazyRef -> {
            if (lazyRef.isLoaded()) {
                lazyRef.clear();
            }
        });
    }

    public void cleanUp(List<String> listKey) {
        Stream<DTO> dtoStream = root.getMapDto().entrySet().stream()
                                    .filter(
                                            map -> !listKey.contains(map.getKey())
                                                   && map.getValue().getLazyUser().isLoaded())
                                    .map(Map.Entry::getValue);

        if (MODE == CACHE_MODE.REFRESH_BY_LAST_ACCESS) {
            dtoStream
                    .sorted(
                            Comparator.comparing(DTO::getLastTouched))
                    .limit(findListKeyNotLoaded(listKey).size())
                    .collect(Collectors.toList())
                    .forEach(dto -> Lazy.clear(dto.getLazyUser()));
        } else if (MODE == CACHE_MODE.REFRESH_BY_USELESS) {
            dtoStream
                    .sorted(
                            Comparator.comparing(DTO::getNumberOfUse))
                    .limit(findListKeyNotLoaded(listKey).size())
                    .collect(Collectors.toList())
                    .forEach(dto -> Lazy.clear(dto.getLazyUser()));
        } else {
            cleanUp();
        }
    }


}
