package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.annotations.Permission;
import io.neow3j.devpack.constants.NativeContract;
import io.neow3j.devpack.contracts.OracleContract;

@Permission(nativeContract = NativeContract.OracleContract)
public class CallOracleContract {

    // Use the OracleMakeRequest example to invoke the request method, and the OracleCheckResponse example to check
    // whether the storage was updated accordingly.
    // Check out the oracle service documentation here: https://docs.neo.org/docs/en-us/advanced/oracle.html

    public static void request(String url, String filter, int gasForResponse) {
        OracleContract.request(url, filter, "callback", null, gasForResponse);
    }

    public static void callback(String url, Object userData, int responseCode, ByteString response) {
        Storage.put(Storage.getStorageContext(), 0, url);
        Storage.put(Storage.getStorageContext(), 1, responseCode);
        Storage.put(Storage.getStorageContext(), 2, response);
    }

    public static String getStoredUrl() {
        return Storage.getString(Storage.getReadOnlyContext(), 0);
    }

    public static int getStoredResponseCode() {
        return Storage.getIntOrZero(Storage.getReadOnlyContext(), 1);
    }

    public static ByteString getStoredResponse() {
        return Storage.get(Storage.getReadOnlyContext(), 2);
    }

}
