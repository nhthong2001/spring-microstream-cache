package vn.elca.demo.model;

import one.microstream.reference.Lazy;

import java.util.List;

public class DTO {
    private int numberOfGet = 0;
    private long lastTouched = 0L;
    private boolean loaded = false;
    private long size = 0;
    private Lazy<List<String>> listUserId;

    public DTO(Lazy<List<String>> listUserId) {
        this.listUserId = listUserId;
    }

    public int getNumberOfGet() {
        return numberOfGet;
    }

    public void setNumberOfGet(int numberOfGet) {
        this.numberOfGet = numberOfGet;
    }

    public long getLastTouched() {
        return lastTouched;
    }

    public void setLastTouched(long lastTouched) {
        this.lastTouched = lastTouched;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public List<String> getListUserId() {
        setNumberOfGet(this.numberOfGet++);
        setLastTouched(System.currentTimeMillis());
        return listUserId.get();
    }

    public void setListUserId(Lazy<List<String>> listUserId) {
        this.listUserId = listUserId;
    }
}
