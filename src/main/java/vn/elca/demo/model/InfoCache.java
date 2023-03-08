package vn.elca.demo.model;

import vn.elca.demo.model.enumType.DataStructure;
import vn.elca.demo.model.enumType.DataType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class InfoCache {
    private DataStructure dataStructure;
    private DataType dataType;
    /*
    * key: dtoId
    *
    * value: dataStructure == Map ? key of data in Map : null
    *
    * use list key of map as a list dtoId of a param
    * */
    private Map<Long, Long> dtoIdMap = new HashMap<>();

    private final AtomicLong lastTouched = new AtomicLong(System.currentTimeMillis());

    public InfoCache() {
    }

    public InfoCache(DataStructure dataStructure, DataType dataType) {
        this.dataStructure = dataStructure;
        this.dataType = dataType;
    }

    public DataStructure getDataStructure() {
        return dataStructure;
    }

    public void setDataStructure(DataStructure dataStructure) {
        this.dataStructure = dataStructure;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Map<Long, Long> getDtoIdMap() {
        return dtoIdMap;
    }

    public void setDtoIdMap(Map<Long, Long> dtoIdMap) {
        this.dtoIdMap = dtoIdMap;
    }

    public long getLastTouched() {
        return lastTouched.get();
    }

    public void setLastTouched(long lastTouched) {
        this.lastTouched.set(lastTouched);
    }

}
