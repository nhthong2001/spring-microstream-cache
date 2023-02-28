package vn.elca.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.elca.demo.model.ShopAvailabilityData;
import vn.elca.demo.service.CacheService;
import vn.elca.demo.service.ShopAvailabilityDataService;

@RestController
public class TestController {

    @Autowired
    CacheService cacheService;

//    @Autowired
//    ShopAvailabilityDataService shopAvailabilityDataService;

//    @GetMapping("initData")
//    public boolean initData() {
//        shopAvailabilityDataService.initData();
//        return true;
//    }

    @GetMapping("get1/{id}")
    public ShopAvailabilityData get1ById(@PathVariable Long id) {
        ShopAvailabilityData result = cacheService.getProductAvailability(null, null, id);
        return result;
    }
    @GetMapping("get2/{id}")
    public ShopAvailabilityData get2ById(@PathVariable Long id) {
        ShopAvailabilityData result = cacheService.getProductAvailability(null, null, null, id);
        return result;
    }

    @GetMapping("putData")
    public ShopAvailabilityData putData(){
        System.out.println("Adding data...");
        long start = System.currentTimeMillis();

        cacheService.init(10_000);

        long end = System.currentTimeMillis();

        System.out.println("Time to push data in ms: " + (end - start));

        return null;
    }

}
