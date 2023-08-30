package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.StorageContext;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.Permission;
import io.neow3j.devpack.annotations.Safe;
import io.neow3j.devpack.annotations.Struct;
import io.neow3j.devpack.constants.NativeContract;
import io.neow3j.devpack.contracts.OracleContract;
import io.neow3j.devpack.contracts.StdLib;

@DisplayName("OracleExampleContract")
@ManifestExtra.ManifestExtras({
        @ManifestExtra(key = "Author", value = "AxLabs"),
        @ManifestExtra(key = "Description", value = "Example contract for using Neo's native Oracle Service")
})
@Permission(nativeContract = NativeContract.OracleContract, methods = "request")
public class CallOracleContract {

    public static final StorageContext ctx = Storage.getStorageContext();
    public static final int lockKey = 0x0f;
    public static final int responseKey = 0xff;

    public static final StdLib stdLib = new StdLib();
    public static final OracleContract oracle = new OracleContract();

    public static void request(String url, String filter) throws Exception {
        if (isLocked()) {
            throw new Exception("Wait until ongoing Oracle request has been finished.");
        }
        lock();
        oracle.request(url, filter, "callback", null, 1000_0000);
    }

    public static void callback(String url, Object userData, int responseCode, ByteString response) throws Exception {
        if (Runtime.getCallingScriptHash() != oracle.getHash()) {
            throw new Exception("No authorization");
        }
        ByteString serialized = stdLib.serialize(new CustomResponseStruct(url, responseCode, response));
        Storage.put(ctx, responseKey, serialized);
        unlock();
    }

    @Safe
    public static CustomResponseStruct getResponse() {
        ByteString serializedResponse = Storage.get(ctx, responseKey);
        return (CustomResponseStruct) stdLib.deserialize(serializedResponse);
    }

    // Some methods to allow only one request at a time.

    private static boolean isLocked() {
        return Storage.getBoolean(ctx, lockKey);
    }

    private static void lock() {
        Storage.put(ctx, lockKey, 1);
    }

    private static void unlock() {
        Storage.put(ctx, lockKey, 0);
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

}
