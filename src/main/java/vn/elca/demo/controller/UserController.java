package vn.elca.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.elca.demo.model.User;
import vn.elca.demo.service.UserService;
import vn.elca.demo.util.MicroStreamCache;
import vn.elca.demo.util.ObjectSizeCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@RestController("user")
public class UserController {
    @Autowired
    private UserService userService;

    private static final MicroStreamCache cache = new MicroStreamCache();

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<User> user_list = userService.getAll();
        return ResponseEntity.ok(user_list);
    }

    @GetMapping("initCache")
    public ResponseEntity<?> testCache() {
        User singleDto = userService.findUserId("1");
        Set<User> set1 = userService.getSet1();
        List<User> list2 = userService.getList2();
        System.out.println("Set 1:");
        System.out.println(ObjectSizeCalculator.getObjectSize(set1));
        System.out.println("List 2:");
        System.out.println(ObjectSizeCalculator.getObjectSize(list2));
        System.out.println("Single dto:");
        System.out.println(ObjectSizeCalculator.getObjectSize(singleDto));
        List<User> users = new ArrayList<>();
        users.addAll(userService.getAll());
        for (int i = 6; i <= 100; i++) {
            users.add(new User(Integer.toString(i), "Name" + i));
        }
        cache.put("singleDto", singleDto);
        cache.put("set1", set1);
        cache.put("list2", list2);
        cache.put("list100", users);

        return ResponseEntity.ok("ok");
    }

    @GetMapping("get/{id}")
    public ResponseEntity<?> getListUser(@PathVariable String id) {
        Object object = cache.get(id);
        if (id.equals("set1")) {
            Set<User> result = (Set<User>) object;
            return ResponseEntity.ok(result);
        } else if (id.equals("list2") || id.equals("list100")) {
            List<User> result = (List<User>) object;
            return ResponseEntity.ok(result);
        } else {
            User result = (User) object;
            return ResponseEntity.ok(result);
        }
    }

}
