package vn.elca.demo.model;

import java.util.concurrent.atomic.AtomicLong;

public class InfoCache {
    private Long shopAvailabilityDataId;
    private final AtomicLong lastTouched = new AtomicLong(System.currentTimeMillis());

    public InfoCache() {
    }

    public InfoCache(Long id) {
        this.shopAvailabilityDataId = id;
    }

    public Long getShopAvailabilityDataId() {

        return shopAvailabilityDataId;
    }

    public void setShopAvailabilityDataId(Long shopAvailabilityDataId) {
        this.shopAvailabilityDataId = shopAvailabilityDataId;
    }

    public long getLastTouched() {
        return lastTouched.get();
    }

    public void setLastTouched(long lastTouched) {
        this.lastTouched.set(lastTouched);
    }

}
