package vn.elca.demo.model;

import java.io.Serializable;

public class AbstractDto implements Serializable {
    protected long id;

    public AbstractDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
