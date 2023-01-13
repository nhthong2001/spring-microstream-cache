package vn.elca.demo.controller;

import one.microstream.storage.types.StorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.elca.demo.database.DB;
import vn.elca.demo.model.Root;
import vn.elca.demo.model.User;
import vn.elca.demo.service.UserService;
import vn.elca.demo.util.MicroStreamCache;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
@RestController("user")
public class UserController {
    @Autowired
    private UserService userService;

    private static final StorageManager storageManager = DB.getInstance();
    private static final Root root = DB.getRoot();
    private MicroStreamCache microStreamCache = new MicroStreamCache();

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<User> user_list = userService.getAll();
        return ResponseEntity.ok(user_list);
    }

    @GetMapping("test")
    public ResponseEntity<?> testCache() throws IOException {
        Set<User> set1 = userService.getSet1();
        List<User> list2 = userService.getList2();
        User singleDto = userService.findUserId("1");
//        System.out.println("Set 1:");
//        System.out.println(ObjectSizeCalculator.getObjectSize(set1));
//        System.out.println(GraphLayout.parseInstance(set1).totalSize());
//        System.out.println("List 2:");
//        System.out.println(ObjectSizeCalculator.getObjectSize(list2));
//        System.out.println(GraphLayout.parseInstance(list2).totalSize());
//        System.out.println("Single dto:");
//        System.out.println(ObjectSizeCalculator.getObjectSize(singleDto));
//        System.out.println(GraphLayout.parseInstance(singleDto).totalSize());

        microStreamCache.put("set1", set1);
        microStreamCache.put("list2", list2);
        microStreamCache.put("singleDto", singleDto);


        return ResponseEntity.ok("ok");
    }

    @GetMapping("get/{id}")
    public ResponseEntity<?> getListUser(@PathVariable String id) {
        Object object = microStreamCache.get(id);

        if (object instanceof Set) {
            Set<User> result = (Set<User>) object;
            return ResponseEntity.ok(result);
        } else if (object instanceof List) {
            List<User> result = (List<User>) object;
            return ResponseEntity.ok(result);
        } else {
            User result = (User) object;
            return ResponseEntity.ok(result);
        }
    }

}
