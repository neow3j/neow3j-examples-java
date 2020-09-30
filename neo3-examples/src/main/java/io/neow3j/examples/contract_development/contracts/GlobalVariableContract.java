package io.neow3j.examples.contract_development.contracts;

import io.neow3j.devpack.annotations.Features;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.neo.Storage;

// A simple contract that makes use of a global variable.
@ManifestExtra(key = "name", value = "GlobalVariableContract")
@Features(hasStorage = true)
public class GlobalVariableContract {

    public static final byte[] anyVar = new byte[] { 0x00, 0x01 };

    public static byte[] entryPoint(String key) {
        return Storage.get(key);
    }

}