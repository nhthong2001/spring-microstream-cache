package vn.elca.demo.database;

import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.types.StorageManager;

import java.time.Duration;

public class MicroStreamDatabase {
    private static StorageManager INSTANCE = null;

    private MicroStreamDatabase() {
    }

    private static StorageManager createStorageManager(Root root) {

        long start = System.currentTimeMillis();
        StorageManager result = EmbeddedStorageConfiguration.Builder()
                                                            .setStorageDirectory("data-test")
                                                            .setChannelCount(4)
                                                            .setHousekeepingInterval(Duration.ofMillis(100))
                                                            .setHousekeepingTimeBudget(Duration.ofNanos(100000000))
                                                            .createEmbeddedStorageFoundation()
                                                            .createEmbeddedStorageManager(root).start();
        long end = System.currentTimeMillis();

        System.out.println("Time to load storage: " + (end - start) + "ms");
        return result;
    }

    public static StorageManager getInstance() {
        // This is not thread safe but for training, we assume only 1 user, the developer and thus no concurrency.
        if (INSTANCE == null) {
            Root root = new Root();
            INSTANCE = createStorageManager(root);
        }
        return INSTANCE;
    }

    public static Root getRoot() {
        // This is not thread safe but for training, we assume only 1 user, the developer and thus no concurrency.
        if (INSTANCE == null) {
            Root root = new Root();
            INSTANCE = createStorageManager(root);
        }
        return (Root) INSTANCE.root();
    }
}
