package vn.elca.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.elca.demo.model.Product;
import vn.elca.demo.model.ShopAvailabilityData;
import vn.elca.demo.service.DemoService;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class TestController {

    @Autowired
    DemoService cacheService;


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

    @GetMapping("get3/{id}")
    public Product get3ById(@PathVariable Long id) {
        Product result = cacheService.getProduct(id);
        return result;
    }

    @GetMapping("get4/{id}")
    public Product get4ById(@PathVariable Long id) {
        Product result = cacheService.getProductDetail(id);
        return result;
    }

    @GetMapping("getListProduct")
    public List<Product> getListProduct() {
        List<Product> result = cacheService.getListProduct();
        return result;
    }

    @GetMapping("getSetShopAvailabilityData")
    public Set<ShopAvailabilityData> getSetShopAvailabilityData() {
        Set<ShopAvailabilityData> result = cacheService.getSetShopAvailabilityData();
        return result;
    }

//    @GetMapping("getPerformanceAvailability")
//    public Map<Long, ShopAvailabilityData> getPerformanceAvailability() {
//        Map<Long, ShopAvailabilityData> result = cacheService.getPerformanceAvailability(null,
//                                                                                   null,
//                                                                                   0,
//                                                                                   null,
//                                                                                   0,
//                                                                                   null,
//                                                                                   null);
//        return result;
//    }


//    @GetMapping("putData/{numberOfObject}")
//    public ShopAvailabilityData putData(@PathVariable int numberOfObject){
//        System.out.println("Adding data...");
//        long start = System.currentTimeMillis();
//
//        cacheService.init(numberOfObject);
//
//        long end = System.currentTimeMillis();
//
//        System.out.println("Time to push data in ms: " + (end - start));
//
//        return null;
//    }

}
