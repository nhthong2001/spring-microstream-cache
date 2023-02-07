package vn.elca.demo.model;

import java.util.ArrayList;
import java.util.List;

public class InfoDto {
    private Dto dto;
    private List<String> listCacheId = new ArrayList<>();

    public InfoDto() {
    }

    public InfoDto(Dto dto, List<String> listCacheId) {
        this.dto = dto;
        this.listCacheId = listCacheId;
    }

    public Dto getDto() {
        return dto;
    }

    public void setDto(Dto dto) {
        this.dto = dto;
    }

    public List<String> getListCacheId() {
        return listCacheId;
    }

    public void setListCacheId(List<String> listCacheId) {
        this.listCacheId = listCacheId;
    }
}
