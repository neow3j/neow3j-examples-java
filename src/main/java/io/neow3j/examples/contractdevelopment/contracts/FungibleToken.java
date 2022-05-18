package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.Contract;
import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.Helper;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.StorageMap;
import io.neow3j.devpack.StringLiteralHelper;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.OnDeployment;
import io.neow3j.devpack.annotations.Permission;
import io.neow3j.devpack.annotations.Safe;
import io.neow3j.devpack.annotations.SupportedStandard;
import io.neow3j.devpack.constants.CallFlags;
import io.neow3j.devpack.constants.NativeContract;
import io.neow3j.devpack.constants.NeoStandard;
import io.neow3j.devpack.contracts.ContractManagement;
import io.neow3j.devpack.events.Event2Args;
import io.neow3j.devpack.events.Event3Args;

@ManifestExtra(key = "name", value = "AxLabsToken")
@ManifestExtra(key = "author", value = "AxLabs")
@SupportedStandard(neoStandard = NeoStandard.NEP_17)
@Permission(nativeContract = NativeContract.ContractManagement)
public class FungibleToken {

    static final StorageMap assetMap = new StorageMap(Storage.getStorageContext(), new byte[]{0x01});
    static final byte[] totalSupplyKey = new byte[]{0x00};

    // region deploy, update, destroy

    @OnDeployment
    public static void deploy(Object data, boolean update) {
        if (!update) {
            if (!Runtime.checkWitness(contractOwner())) {
                fireErrorAndAbort("No authorization.", "deploy");
            }
            // Initialize the supply
            int initialSupply = 200_000_000;
            Storage.put(Storage.getStorageContext(), totalSupplyKey, initialSupply);
            // Allocate all tokens to the contract owner.
            assetMap.put(contractOwner().toByteArray(), initialSupply);
        }
    }

    public static void update(ByteString script, String manifest) {
        if (!Runtime.checkWitness(contractOwner())) {
            fireErrorAndAbort("No authorization.", "update");
        }
        ContractManagement.update(script, manifest);
    }

    public static void destroy() {
        if (!Runtime.checkWitness(contractOwner())) {
            fireErrorAndAbort("No authorization.", "destroy");
        }
        ContractManagement.destroy();
    }

    // endregion deploy, update, destroy
    // region NEP-17 methods

    @Safe
    public static String symbol() {
        return "ALT";
    }

    @Safe
    public static int decimals() {
        return 2;
    }

    @Safe
    public static int totalSupply() {
        return Storage.getInt(Storage.getReadOnlyContext(), totalSupplyKey);
    }

    public static boolean transfer(Hash160 from, Hash160 to, int amount, Object[] data) throws Exception {
        if (!Hash160.isValid(from) || !Hash160.isValid(to)) {
            throw new Exception("The parameters 'from' and 'to' must be 20-byte addresses.");
        }
        if (amount < 0) {
            throw new Exception("The parameter 'amount' must be greater than or equal to 0.");
        }
        if (amount > getBalance(from) || !Runtime.checkWitness(from)) {
            return false;
        }

        if (from != to && amount != 0) {
            deductFromBalance(from, amount);
            addToBalance(to, amount);
        }

        onTransfer.fire(from, to, amount);
        if (ContractManagement.getContract(to) != null) {
            Contract.call(to, "onNEP17Payment", CallFlags.All, data);
        }
        return true;
    }

    @Safe
    public static int balanceOf(Hash160 account) throws Exception {
        if (!Hash160.isValid(account)) {
            throw new Exception("The parameter 'account' must be a 20-byte address.");
        }
        return getBalance(account);
    }

    // endregion NEP-17 methods
    // region events

    @DisplayName("Transfer")
    static Event3Args<Hash160, Hash160, Integer> onTransfer;

    /**
     * This event is intended to be fired before aborting the VM. The first argument should be a message and the
     * second argument should be the method name whithin which it has been fired.
     */
    @DisplayName("Error")
    private static Event2Args<String, String> error;

    // endregion events
    // region custom methods

    @Safe
    public static Hash160 contractOwner() {
        return StringLiteralHelper.addressToScriptHash("NM7Aky765FG8NhhwtxjXRx7jEL1cnw7PBP");
    }

    // endregion custom methods
    // region private helper methods

    private static void fireErrorAndAbort(String msg, String method) {
        error.fire(msg, method);
        Helper.abort();
    }

    private static void addToBalance(Hash160 key, int value) {
        assetMap.put(key.toByteArray(), getBalance(key) + value);
    }

    private static void deductFromBalance(Hash160 key, int value) {
        int oldValue = getBalance(key);
        assetMap.put(key.toByteArray(), oldValue - value);
    }

    private static int getBalance(Hash160 key) {
        return assetMap.getIntOrZero(key.toByteArray());
    }

    // endregion private helper methods

}
