package vn.elca.demo.database;

import one.microstream.afs.blobstore.types.BlobStoreFileSystem;
import one.microstream.afs.redis.types.RedisConnector;
import one.microstream.reference.Lazy;
import one.microstream.reference.LazyReferenceManager;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.types.*;

import java.time.Duration;

public class DB {
    private static StorageManager INSTANCE = null;

    private DB() {
    }

    private static StorageManager createStorageManager(Root root) {
//        LazyReferenceManager a = LazyReferenceManager.set(LazyReferenceManager.New(
//                Lazy.Checker(
//                        Duration.ofMinutes(30).toMillis(), // timeout of lazy access
//                        0.75                               // memory quota
//                )
//        ));
        String redisUri = "redis://localhost:6379/0";
        BlobStoreFileSystem fileSystem = BlobStoreFileSystem.New(
                RedisConnector.Caching(redisUri)
        );
//        StorageManager result = EmbeddedStorageConfiguration.Builder()
//                .setStorageDirectory("data-cache")
//                .setHousekeepingInterval(Duration.ofMillis(100))
//                .setHousekeepingTimeBudget(Duration.ofNanos(100000000))
//                .setDataFileMinimumUseRatio(1)
//                .createEmbeddedStorageFoundation()
//                .createEmbeddedStorageManager(root).start();
        StorageManager result = EmbeddedStorageFoundation.New()
                .setConfiguration(
                        StorageConfiguration.Builder()
                                .setStorageFileProvider(
                                        Storage.FileProviderBuilder(fileSystem)
                                                .setDirectory(fileSystem.ensureDirectoryPath("redis-microstream"))
                                                .createFileProvider()
                                )
                                .setChannelCountProvider(StorageChannelCountProvider.New(1))
                                .setHousekeepingController(Storage.HousekeepingController(100, 100000000))
                                .createConfiguration()
                )
                .createEmbeddedStorageManager(root).start();
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
