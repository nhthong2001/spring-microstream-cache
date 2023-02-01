package vn.elca.demo.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Root {

    private final Map<String, Dto> mapDto = new ConcurrentHashMap<>();
    private final Map<String, CacheDto> mapIdRequestWithCacheDto = new ConcurrentHashMap<>();

    public Map<String, Dto> getMapDto() {
        return mapDto;
    }

    public Map<String, CacheDto> getMapIdRequestWithCacheDto() {
        return mapIdRequestWithCacheDto;
    }

}
