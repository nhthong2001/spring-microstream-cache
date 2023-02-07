package vn.elca.demo.model;

import java.util.concurrent.atomic.AtomicLong;

public class InfoCache {
    private Type type;
    private final AtomicLong lastTouched = new AtomicLong();

    public InfoCache() {
    }

    public InfoCache(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public long getLastTouched() {
        return lastTouched.get();
    }

    public void setLastTouched(long lastTouched) {
        this.lastTouched.set(lastTouched);
    }

}
