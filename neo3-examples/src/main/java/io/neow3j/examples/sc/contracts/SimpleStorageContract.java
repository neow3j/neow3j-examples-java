package io.neow3j.examples.sc.contracts;

import io.neow3j.devpack.framework.Storage;
import io.neow3j.devpack.framework.annotations.EntryPoint;
import io.neow3j.devpack.framework.annotations.Features;
import io.neow3j.devpack.framework.annotations.ManifestExtra;

// A simple contract that allows putting and getting from it's storage area.
@ManifestExtra(key = "name", value = "SimpleStorageContract")
@Features(hasStorage = true)
public class SimpleStorageContract {

    @EntryPoint
    public static boolean putSomething(String key, String value) {
        Storage.put(key, value);
        return true;
    }

    public static byte[] getSomething(String key) {
        return Storage.get(key);
    }

}