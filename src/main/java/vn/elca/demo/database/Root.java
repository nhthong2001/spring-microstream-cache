package vn.elca.demo.database;

import vn.elca.demo.model.AbstractDto;
import vn.elca.demo.model.InfoCache;
import vn.elca.demo.model.Params;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Root {

    private final ConcurrentHashMap<String, AbstractDto> mapData = new ConcurrentHashMap<>();
    private final  ConcurrentHashMap<Params, InfoCache> mapInfoCache = new ConcurrentHashMap<>();
    private final  ConcurrentHashMap<String, List<Params>> mapInfoData = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, AbstractDto> getMapData() {
        return mapData;
    }

    public ConcurrentHashMap<Params, InfoCache> getMapInfoCache() {
        return mapInfoCache;
    }

    public ConcurrentHashMap<String, List<Params>> getMapInfoData() {
        return mapInfoData;
    }
}
