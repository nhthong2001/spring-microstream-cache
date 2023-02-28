package vn.elca.demo.model;

public class Product extends AbstractDto {
    private String name;
    private long quantity;
    private Long quota;

    public Product() {
    }

    public Product(long id, String name, long quantity, Long quota) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.quota = quota;
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
}
