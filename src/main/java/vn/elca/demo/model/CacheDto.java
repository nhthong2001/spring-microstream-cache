package vn.elca.demo.model;

import one.microstream.reference.Lazy;

import java.util.List;

public class CacheDto {
    private Type type;
    private Lazy<List<String>> listUserId;

    public CacheDto(Lazy<List<String>> listUserId) {
        this.listUserId = listUserId;
    }


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Lazy<List<String>> getListUserId() {
        return listUserId;
    }

}
