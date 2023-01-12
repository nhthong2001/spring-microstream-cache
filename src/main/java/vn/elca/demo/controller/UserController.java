package vn.elca.demo.controller;

import one.microstream.storage.types.StorageManager;
import org.openjdk.jol.info.GraphLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.elca.demo.database.DB;
import vn.elca.demo.database.Root;
import vn.elca.demo.model.User;
import vn.elca.demo.service.UserService;
import vn.elca.demo.util.ObjectSizeCalculator;

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
        System.out.println("Set 1:");
        System.out.println(ObjectSizeCalculator.getObjectSize(set1));
        System.out.println(GraphLayout.parseInstance(set1).totalSize());
        System.out.println("List 2:");
        System.out.println(ObjectSizeCalculator.getObjectSize(list2));
        System.out.println(GraphLayout.parseInstance(list2).totalSize());
        System.out.println("Single dto:");
        System.out.println(ObjectSizeCalculator.getObjectSize(singleDto));
        System.out.println(GraphLayout.parseInstance(singleDto).totalSize());

        root.save(set1, "set1");
        root.save(list2, "list2");
        root.save(singleDto, "singleDto");

        storageManager.store(root.getUsers());
        storageManager.store(root.getMapIdRequestWithListUserId());

        return ResponseEntity.ok("ok");
    }

    @GetMapping("get/{id}")
    public ResponseEntity<?> getListUser(@PathVariable String id) throws IOException {
        if (id.equals("set1")) {
            Set<User> result = (Set<User>) root.getUsersById(id);
//            System.out.println(ObjectSizeFetcher.serialize(result));
            return ResponseEntity.ok(result);
        } else if (id.equals("list2")) {
            List<User> result = (List<User>) root.getUsersById(id);
            return ResponseEntity.ok(result);
        } else {
            User result = (User) root.getUsersById(id);
            return ResponseEntity.ok(result);
        }
    }

}
