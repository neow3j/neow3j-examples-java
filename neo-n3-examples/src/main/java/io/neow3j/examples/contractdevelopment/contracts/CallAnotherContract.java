package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.CallFlags;
import io.neow3j.devpack.Contract;
import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.annotations.ManifestExtra;

@ManifestExtra(key = "author", value = "AxLabs")
public class CallAnotherContract {

    // You can use these two methods with the contract `SimpleStorageContract`.

    public static ByteString getFromAnotherContract(Hash160 simpleStorageContract, String key) {
        // If the return is needed, it has to be cast to the type that is expected.
        return (ByteString) Contract.call(simpleStorageContract, "getSomething",
                CallFlags.READ_ONLY, new Object[]{key});
    }

    public static void putOnAnotherContract(Hash160 simpleStorageContract, String key,
            String value) {
        // In this method, you could cast the return to a boolean (since `putSomething` has a
        // boolean as return type), if you want to check whether the method `putSomething` was
        // successful.
        // However, if the return is not needed, no cast is necessary.
        Contract.call(simpleStorageContract, "putSomething", CallFlags.ALL,
                new Object[]{key, value});
    }

}
