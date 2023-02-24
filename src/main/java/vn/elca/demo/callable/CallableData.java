package vn.elca.demo.callable;

import vn.elca.demo.service.CacheService;

import java.util.concurrent.Callable;

public class CallableData implements Callable<Void> {
    private CacheService cacheService;

    private int startNumber;
    private int endNumber;

    public CallableData(CacheService cacheService, int startNumber, int endNumber) {
        this.cacheService = cacheService;
        this.startNumber = startNumber;
        this.endNumber = endNumber;
    }

    @Override
    public Void call() {
        for (int i = startNumber; i <= endNumber; i++) {
            cacheService.getProductAvailability(null, null, null, i);
        }

        return null;
    }
}

