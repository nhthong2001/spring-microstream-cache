package vn.elca.demo.model;

import vn.elca.demo.model.annotation.Check;

import java.util.Objects;

public class Product extends AbstractDto {
    private String name;
    private long quantity;
    private Long quota;

    @Check
    private ShopAvailabilityData shopAvailabilityData;

    public Product() {
    }

    public Product(long id, String name, long quantity, Long quota, ShopAvailabilityData shopAvailabilityData) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.quota = quota;
        this.shopAvailabilityData = shopAvailabilityData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public Long getQuota() {
        return quota;
    }

    public void setQuota(Long quota) {
        this.quota = quota;
    }

    public ShopAvailabilityData getShopAvailabilityData() {
        return shopAvailabilityData;
    }

    public void setShopAvailabilityData(ShopAvailabilityData shopAvailabilityData) {
        this.shopAvailabilityData = shopAvailabilityData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, quantity, quota);
    }
}
