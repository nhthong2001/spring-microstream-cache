package vn.elca.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.elca.demo.callable.CallableData;
import vn.elca.demo.model.ShopAvailabilityData;
import vn.elca.demo.model.enumType.ShopAvailabilityLevel;
import vn.elca.demo.service.CacheService;
import vn.elca.demo.service.ShopAvailabilityDataService;
import vn.elca.demo.util.MicroStreamCache;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class TestController {

    @Autowired
    CacheService cacheService;

    @Autowired
    ShopAvailabilityDataService shopAvailabilityDataService;

    @GetMapping("initData")
    public boolean initData() {
        shopAvailabilityDataService.initData();
        return true;
    }

    @GetMapping("get/{id}")
    public ShopAvailabilityData getById(@PathVariable Long id) {
        ShopAvailabilityData result = cacheService.getProductAvailability(null, null, id);
        return result;
    }

    @GetMapping("putData")
    public ShopAvailabilityData putData() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<CallableData> callables = new ArrayList<>();
        CallableData callableData;

        long start = System.currentTimeMillis();
        for (int i = 0; i <= 9; i++) {
            callableData = new CallableData(cacheService, i * 100_000 + 1, i * 100_000 + 100_000);
            callables.add(callableData);
        }
        executorService.invokeAll(callables);

        long end = System.currentTimeMillis();

        System.out.println("Time to push 1M data in ms: " + (end - start));

        return null;
    }

}
