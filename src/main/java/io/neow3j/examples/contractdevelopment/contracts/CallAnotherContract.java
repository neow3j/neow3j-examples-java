package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.Contract;
import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.annotations.CallFlags;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.Permission;
import io.neow3j.devpack.contracts.ContractInterface;

import static io.neow3j.devpack.constants.CallFlags.ReadOnly;
import static io.neow3j.devpack.constants.CallFlags.WriteStates;

@ManifestExtra(key = "author", value = "AxLabs")
// Set the permissions to any contract because the contract called from within this contract is dynamic.
@Permission(contract = "*", methods = "*")
public class CallAnotherContract {

    public static void putOnAnotherContract(Hash160 simpleStorageContract, String key, String value) {
        new SimpleStorageContractInterface(simpleStorageContract).putSomething(key, value);
    }

    // You can use these two methods with the contract `SimpleStorageContract`.
    public static ByteString getFromAnotherContract(Hash160 simpleStorageContract, String key) {
        // If you do not want to use a contract interface, you can use Contract.call to dynamically call a method of
        // another contract. If you require the return value, make sure to cast it to the type that is expected.
        return (ByteString) Contract.call(simpleStorageContract, "getSomething", ReadOnly, new Object[]{key});
    }

    static class SimpleStorageContractInterface extends ContractInterface {
        public SimpleStorageContractInterface(Hash160 contractHash) {
            super(contractHash);
        }

        @CallFlags(WriteStates) // Note, that the absensce of this annotation defaults to CallFlags.All for this method.
        public native ByteString putSomething(String key, String value);
    }

}
