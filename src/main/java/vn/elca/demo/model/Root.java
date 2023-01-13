package vn.elca.demo.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Root {

    private final Map<String, DTO> mapDto = new ConcurrentHashMap<>();
    private final Map<String, ObjectDTO> mapIdRequestWithObjectDTO = new ConcurrentHashMap<>();

    public Map<String, DTO> getMapDto() {
        return mapDto;
    }

    public Map<String, ObjectDTO> getMapIdRequestWithObjectDTO() {
        return mapIdRequestWithObjectDTO;
    }

}
