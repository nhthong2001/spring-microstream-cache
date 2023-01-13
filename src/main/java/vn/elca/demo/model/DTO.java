package vn.elca.demo.model;

import one.microstream.reference.Lazy;

public class DTO {
    private long numberOfUse = 0;

    private Lazy<User> user;

    public DTO(Lazy<User> user) {
        this.user = user;
    }

    public DTO() {
    }

    public Lazy<User> getLazyUser() {
        return user;
    }

    public long getLastTouched() {
        return user.lastTouched();
    }

    public User getUser() {
        numberOfUse++;
        return user.get();
    }

    public void setUser(User user) {
        this.user = Lazy.Reference(user);
    }

    public long getNumberOfUse() {
        return numberOfUse;
    }

}
