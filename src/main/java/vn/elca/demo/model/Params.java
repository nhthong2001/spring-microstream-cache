package vn.elca.demo.model;

import java.util.Objects;

public class Params {
    private String paramKey;

    public Params(String paramKey) {
        this.paramKey = paramKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Params params = (Params) o;
        return paramKey.equals(params.paramKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paramKey);
    }
}
