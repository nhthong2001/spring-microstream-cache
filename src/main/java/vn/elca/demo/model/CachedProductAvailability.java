package vn.elca.demo.model;

public class CachedProductAvailability {
    private long productId;
    private ShopAvailabilityData computedAvailability;

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public ShopAvailabilityData getComputedAvailability() {
        return computedAvailability;
    }

    public void setComputedAvailability(ShopAvailabilityData computedAvailability) {
        this.computedAvailability = computedAvailability;
    }
}
