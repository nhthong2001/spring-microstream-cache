package vn.elca.demo.controller;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.elca.demo.model.Product;
import vn.elca.demo.model.ShopAvailabilityData;
import vn.elca.demo.model.annotation.CustomMicrostreamCached;
import vn.elca.demo.service.DemoService;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class TestController {

    @Autowired
    DemoService demoService;


    @GetMapping("get1/{id}")
    public ShopAvailabilityData get1ById(@PathVariable Long id) {
        ShopAvailabilityData result = demoService.getProductAvailability(null, null, id);
        return result;
    }

    @GetMapping("get2/{id}")
    public ShopAvailabilityData get2ById(@PathVariable Long id) {
        ShopAvailabilityData result = demoService.getProductAvailability(null, null, null, id);
        return result;
    }

    @GetMapping("get3/{id}")
    public Product get3ById(@PathVariable Long id) {
        Product result = demoService.getProduct(id);
        return result;
    }

    @GetMapping("get4/{id}")
    public Product get4ById(@PathVariable Long id) {
        Product result = demoService.getProductDetail(id);
        return result;
    }

    @GetMapping("getListProduct")
    public List<Product> getListProduct() {
        List<Product> result = demoService.getListProduct();
        return result;
    }

    @GetMapping("getSetShopAvailabilityData")
    public Set<ShopAvailabilityData> getSetShopAvailabilityData() {
        Set<ShopAvailabilityData> result = demoService.getSetShopAvailabilityData();
        return result;
    }



    @GetMapping("getPerformanceAvailability")
    public Map<Long, ShopAvailabilityData> getPerformanceAvailability() {
        Map<Long, ShopAvailabilityData> result = demoService.getPerformanceAvailability(null,
                                                                                        null,
                                                                                        0,
                                                                                        null,
                                                                                        0,
                                                                                        null,
                                                                                        null);
        return result;
    }


    @GetMapping("test/{numberOfObject}")
    public ShopAvailabilityData test(@PathVariable int numberOfObject){
        System.out.println("Adding data...");
        long start = System.currentTimeMillis();

        for (int i = 1; i <= numberOfObject; i++) {
            demoService.getProductAvailability(null, null, null, i);
        }

        long end = System.currentTimeMillis();

        System.out.println("Time to test data in ms: " + (end - start));

        return null;
    }

    // DEMO
    @GetMapping("getProductById/{id}")
    public Product getProductById(@PathVariable long id) {
        Product result = demoService.getProductDetail(id);
        return result;
    }
    @GetMapping("getShopAvailabilityDataById/{id}")
    public ShopAvailabilityData getShopAvailabilityDataById(@PathVariable long id) {
        ShopAvailabilityData result = demoService.getShopAvailabilityDataById(id);
        return result;
    }

    @GetMapping("getAll")
    public List<ShopAvailabilityData> getAll() {
        List<ShopAvailabilityData> result = demoService.getAllShopAvailabilityData();
        return result;
    }

    @GetMapping("getListShopAvailabilityData")
    public List<ShopAvailabilityData> getListShopAvailabilityData(@Param("from") long from, @Param("to") long to) {
        List<ShopAvailabilityData> result = demoService.getListShopAvailabilityData(from, to);
        return result;
    }

    @GetMapping("getListShopAvailabilityDataCompress")
    public List<ShopAvailabilityData> getListShopAvailabilityDataCompress(@Param("from") long from, @Param("to") long to) {
        List<ShopAvailabilityData> result = demoService.getListShopAvailabilityDataCompress(from, to);
        return result;
    }



}
