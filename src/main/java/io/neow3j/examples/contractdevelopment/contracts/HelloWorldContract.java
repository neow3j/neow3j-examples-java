package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.Safe;

// A simple smart contract with one method that returns a string and takes no arguments.
@ManifestExtra(key = "name", value = "HelloWorldContract")
public class HelloWorldContract {

    @Safe
    public static String bongoCat() {
        return "neowwwwwwwwww";
    }

}
