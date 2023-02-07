package vn.elca.demo.database;

import vn.elca.demo.model.Dto;
import vn.elca.demo.model.InfoCache;
import vn.elca.demo.model.InfoDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Root {

    private final Map<String, Dto> mapDto = new HashMap<>();
    private final Map<String, Set<Dto>> mapSetDto = new HashMap<>();
    private final Map<String, List<Dto>> mapListDto = new HashMap<>();

    private final Map<String, InfoCache> mapInfoCache = new HashMap<>();

    private final Map<String, InfoDto> mapInfoDto = new HashMap<>();



    public Map<String, Dto> getMapDto() {
        return mapDto;
    }

    public Map<String, Set<Dto>> getMapSetDto() {
        return mapSetDto;
    }

    public Map<String, List<Dto>> getMapListDto() {
        return mapListDto;
    }

    public Map<String, InfoCache> getMapInfoCache() {
        return mapInfoCache;
    }

    public Map<String, InfoDto> getMapInfoDto() {
        return mapInfoDto;
    }
}
