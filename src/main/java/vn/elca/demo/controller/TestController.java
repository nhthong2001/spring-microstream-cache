package vn.elca.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.elca.demo.service.FakeDtoService;
import vn.elca.demo.util.MicroStreamCache;

import java.io.IOException;

@RestController
public class TestController {
    @Autowired
    MicroStreamCache cache;

    @Autowired
    FakeDtoService dtoService;

//    @GetMapping("add")
//    public boolean add1() throws IOException {
//        Dto dto1 = new Dto("1", Files.readAllBytes(Paths.get("D:/100MB.bin")));
//        Dto dto2 = new Dto("2", Files.readAllBytes(Paths.get("D:/100MB-2.bin")));
//        Dto dto3 = new Dto("3", Files.readAllBytes(Paths.get("D:/100MB-3.bin")));
//
//        List<Dto> list = new ArrayList<>();
//        list.add(dto1);
//        list.add(dto2);
//
//        Set<Dto> set = new HashSet<>();
//        set.add(dto1);
//        set.add(dto3);
//
//        cache.put("singleDto", dto1);
//
//        cache.put("set", set);
//
//        cache.put("list", list);
//
//        System.out.println("Cache successfully!");
//        return true;
//    }
//
//    @GetMapping("add/{id}")
//    public boolean addTest(@PathVariable(name = "id") String id) throws IOException {
//        Dto dto3 = new Dto("3", Files.readAllBytes(Paths.get("D:/100MB.bin")));
//        Dto dto4 = new Dto("4", Files.readAllBytes(Paths.get("D:/100MB-2.bin")));
//        Dto dto5 = new Dto("5", Files.readAllBytes(Paths.get("D:/100MB-3.bin")));
//
//        List<Dto> list = new ArrayList<>();
//        list.add(dto3);
//        list.add(dto4);
//        list.add(dto5);
//
//        cache.put("list" + id, list);
//
//        System.out.println("Cache successfully!");
//        return true;
//    }

    @GetMapping("getAll")
    public boolean getAll() throws IOException {

        long start = System.currentTimeMillis();
        cache.get("singleDto");
        cache.get("set");
        cache.get("list");
        long end = System.currentTimeMillis();
        System.out.println("Time to load all: " + (end - start) + "ms");

        return true;

    }
    @GetMapping("getTest")
    public Long getTest() throws InterruptedException {

        long start = System.currentTimeMillis();
        if (cache.get("singleDto") == null) {
            cache.put("singleDto", dtoService.get("singleDto"));
        };

        if (cache.get("set") == null) {
            cache.put("set", dtoService.get("set"));
        };

        if (cache.get("list") == null) {
            cache.put("list", dtoService.get("list"));
        };

//        if (cache.get("something") == null) {
//            cache.put("something", dtoService.get("something"));
//        };

        long end = System.currentTimeMillis();
        System.out.println("Time to load all: " + (end - start) + "ms");

        return end - start;

    }

    @GetMapping("get1")
    public boolean get1() {
        long start = System.currentTimeMillis();
        cache.get("singleDto");
//        System.out.println("MicroStreamSerializer get size: " +MicroStreamSerializer.getSize(dto1));
//        System.out.println("ObjectSizeCalculator get size: " + ObjectSizeCalculator.getObjectSize(dto1));

        long end = System.currentTimeMillis();
        System.out.println("Time to load dto 1: " + (end - start) + "ms");

        return true;

    }

    @GetMapping("get2")
    public boolean get2() {
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
        System.out.println("Time to load dto 2: " + (end - start) + "ms");
        cache.get("set");
        return true;
    }

    @GetMapping("get3")
    public boolean get3() {
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
        System.out.println("Time to load dto 2: " + (end - start) + "ms");
        cache.get("list");
        return true;
    }
}
