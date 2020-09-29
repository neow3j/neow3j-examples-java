package io.neow3j.examples.sc.contracts;

import io.neow3j.devpack.framework.Storage;
import io.neow3j.devpack.framework.annotations.EntryPoint;
import io.neow3j.devpack.framework.annotations.Features;
import io.neow3j.devpack.framework.annotations.ManifestExtra;

// A simple contract that makes use of a global variable.
@ManifestExtra(key = "name", value = "GlobalVariableContract")
@Features(hasStorage = true)
public class GlobalVariableContract {

    public static final byte[] anyVar = new byte[] { 0x00, 0x01 };

    @EntryPoint
    public static byte[] entryPoint(String key) {
        return Storage.get(key);
    }

}