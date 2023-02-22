package vn.elca.demo.database;

import vn.elca.demo.model.Dto;
import vn.elca.demo.model.InfoCache;
import vn.elca.demo.model.InfoDto;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Root {

    private final ConcurrentHashMap<String, Dto> mapDto = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<Dto>> mapSetDto = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<Dto>> mapListDto = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, InfoCache> mapInfoCache = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, InfoDto> mapInfoDto = new ConcurrentHashMap<>();



    public ConcurrentHashMap<String, Dto> getMapDto() {
        return mapDto;
    }

    public ConcurrentHashMap<String, Set<Dto>> getMapSetDto() {
        return mapSetDto;
    }

    public ConcurrentHashMap<String, List<Dto>> getMapListDto() {
        return mapListDto;
    }

    public ConcurrentHashMap<String, InfoCache> getMapInfoCache() {
        return mapInfoCache;
    }

    public ConcurrentHashMap<String, InfoDto> getMapInfoDto() {
        return mapInfoDto;
    }
}
