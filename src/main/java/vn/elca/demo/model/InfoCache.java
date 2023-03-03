package vn.elca.demo.model;

import vn.elca.demo.model.enumType.DataStructure;
import vn.elca.demo.model.enumType.DataType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class InfoCache {
    private DataStructure dataStructure;
    private DataType dataType;
    private List<Long> listDtoId = new ArrayList<>();
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

    public List<Long> getListDtoId() {
        return listDtoId;
    }

    public void setListDtoId(List<Long> listDtoId) {
        this.listDtoId = listDtoId;
    }

    public long getLastTouched() {
        return lastTouched.get();
    }

    public void setLastTouched(long lastTouched) {
        this.lastTouched.set(lastTouched);
    }

}
