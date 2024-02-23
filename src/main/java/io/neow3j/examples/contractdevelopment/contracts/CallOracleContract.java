package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.Helper;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.StorageContext;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.OnDeployment;
import io.neow3j.devpack.annotations.Permission;
import io.neow3j.devpack.annotations.Safe;
import io.neow3j.devpack.annotations.Struct;
import io.neow3j.devpack.constants.NativeContract;
import io.neow3j.devpack.contracts.ContractManagement;
import io.neow3j.devpack.contracts.OracleContract;
import io.neow3j.devpack.contracts.StdLib;

@DisplayName("OracleExampleContract")
@ManifestExtra.ManifestExtras({
        @ManifestExtra(key = "Author", value = "AxLabs"),
        @ManifestExtra(key = "Description", value = "Example contract for using Neo's native Oracle Service")
})
@Permission(nativeContract = NativeContract.OracleContract, methods = "request")
@Permission(nativeContract = NativeContract.ContractManagement)
public class CallOracleContract {

    public static final StorageContext ctx = Storage.getStorageContext();
    public static final int responseKey = 0x01;

    public static final StdLib stdLib = new StdLib();
    public static final OracleContract oracle = new OracleContract();

    public static void request(String url, String filter, int gasForResponse) {
        oracle.request(url, filter, "callback", null, gasForResponse);
    }

    public static void callback(String url, Object userData, int responseCode, ByteString response) throws Exception {
        if (Runtime.getCallingScriptHash() != oracle.getHash()) {
            throw new Exception("No authorization");
        }
        ByteString serialized = stdLib.serialize(new CustomResponseStruct(url, responseCode, response));
        Storage.put(ctx, responseKey, serialized);
    }

    @Safe
    public static CustomResponseStruct getResponse() {
        ByteString serializedResponse = Storage.get(ctx, responseKey);
        return (CustomResponseStruct) stdLib.deserialize(serializedResponse);
    }

    @Struct
    public static class CustomResponseStruct {
        public String url;
        public int responseCode;
        public ByteString response;

        public CustomResponseStruct(String url, int responseCode, ByteString response) {
            this.url = url;
            this.responseCode = responseCode;
            this.response = response;
        }

    }

    // The following part of the contract is not relevant for this example and doesn't interfere with the above code.
    // It is simply there to maintain the contract on testnet.

    public static final int ownerKey = 0x00;

    @OnDeployment
    public static void deploy(Object owner, boolean isUpdate) {
        if (!isUpdate) {
            Hash160 ownerHash = (Hash160) owner;
            if (!Hash160.isValid(ownerHash)) Helper.abort("Deployment data requires a Hash160 value.");
            Storage.put(ctx, ownerKey, ownerHash);
        }
    }

    public static void setOwner(Hash160 newOwner) {
        if (!Runtime.checkWitness(owner())) Helper.abort("No authorization");
        Storage.put(ctx, ownerKey, newOwner);
    }

    @Safe
    public static Hash160 owner() {
        return Storage.getHash160(ctx, ownerKey);
    }

    public static void update(ByteString nef, String manifest) {
        if (!Runtime.checkWitness(owner())) Helper.abort("No authorization");
        new ContractManagement().update(nef, manifest);
    }

    public static void destroy() {
        if (!Runtime.checkWitness(owner())) Helper.abort("No authorization");
        new ContractManagement().destroy();
    }

}
