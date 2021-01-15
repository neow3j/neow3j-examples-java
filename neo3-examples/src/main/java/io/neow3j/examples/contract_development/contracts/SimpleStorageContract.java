package io.neow3j.examples.contract_development.contracts;

import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.neo.Storage;

// A simple contract that allows putting and getting from it's storage area.
@ManifestExtra(key = "name", value = "SimpleStorageContract")
public class SimpleStorageContract {

    public static boolean putSomething(String key, String value) {
        Storage.put(key, value);
        return true;
    }

    public static byte[] getSomething(String key) {
        return Storage.get(key);
    }
}
