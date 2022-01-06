package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.StorageContext;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.Permission;

@ManifestExtra(key = "name", value = "ExampleContract")
@Permission(contract = "*")
public class ExampleContract {

    static StorageContext ctx = Storage.getStorageContext();

    public static ByteString method(String key) {
        String prefix = "storage_prefix";
        return Storage.get(ctx, prefix + key);
        // return Storage.get(Storage.getStorageContext(), prefix + key);
    }
}