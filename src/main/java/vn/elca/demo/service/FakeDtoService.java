package vn.elca.demo.service;

import org.springframework.stereotype.Service;
import vn.elca.demo.model.Dto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FakeDtoService {

    static Dto dto1, dto2, dto3, dto4, dto5;

    static {
        try {
            dto1 = new Dto("1", Files.readAllBytes(Paths.get("D:/Data/100MB-1.bin")));
            dto2 = new Dto("2", Files.readAllBytes(Paths.get("D:/Data/100MB-2.bin")));
            dto3 = new Dto("3", Files.readAllBytes(Paths.get("D:/Data/100MB-3.bin")));
            dto4 = new Dto("4", Files.readAllBytes(Paths.get("D:/Data/100MB-4.bin")));
            dto5 = new Dto("5", Files.readAllBytes(Paths.get("D:/Data/100MB-5.bin")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FakeDtoService() {
    }

    public Object get(String id) throws InterruptedException {

        if (id.equals("singleDto")) {
            Thread.sleep(1000);
            return dto1;
        }

        if (id.equals("set")) {
            Set<Dto> set = new HashSet<>();
            set.add(dto1);
            set.add(dto2);

            Thread.sleep(2000);
            return set;
        }

        if (id.equals("list")) {
            List<Dto> list = new ArrayList<>();
            list.add(dto1);
            list.add(dto3);

            Thread.sleep(2000);
            return list;
        }

        List<Dto> list = new ArrayList<>();
        list.add(dto3);
        list.add(dto4);
        list.add(dto5);

        Thread.sleep(2000);
        return list;
    }
}
