package vn.elca.demo.database;

import one.microstream.reference.Lazy;
import one.microstream.storage.types.StorageManager;
import vn.elca.demo.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Root {

    private final Map<String, Lazy<User>> users = new ConcurrentHashMap<>();
    private final Map<String, Lazy<List<String>>> mapIdRequestWithListUserId = new ConcurrentHashMap<>();


    public Map<String, Lazy<User>> getUsers() {
        return users;
    }

    public Map<String, Lazy<List<String>>> getMapIdRequestWithListUserId() {
        return mapIdRequestWithListUserId;
    }

    public void save(Collection<User> collection, String id) {
        collection.forEach(user -> {
            if (!users.containsKey(user.getId())) {
                users.put(user.getId(), Lazy.Reference(user));
            }
        });

        List<String> listUserId = collection.stream().map(User::getId).collect(Collectors.toList());
        mapIdRequestWithListUserId.put(id, Lazy.Reference(listUserId));
    }
    public void save(User user, String id) {
        if (!users.containsKey(user.getId())) {
            users.put(user.getId(), Lazy.Reference(user));
        }

        List<String> listUserId = new ArrayList<>();
        listUserId.add(user.getId());
        mapIdRequestWithListUserId.put(id, Lazy.Reference(listUserId));
    }

    public Object getUsersById(String id) {
        List<String> ids = mapIdRequestWithListUserId.get(id).get();
        if (ids.size() == 1) {
            return users.get(ids.get(0)).get();
        }

        Collection<User> result;
        if (id.equalsIgnoreCase("set1")) {
            result = new HashSet<>();
        } else {
            result = new ArrayList<>();
        }
        ids.forEach(tempId -> {
            result.add(users.get(tempId).get());
        });

        return result;
    }
}
