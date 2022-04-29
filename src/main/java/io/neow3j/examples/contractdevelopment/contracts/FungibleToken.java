package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.Contract;
import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.StorageContext;
import io.neow3j.devpack.StorageMap;
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
import io.neow3j.devpack.events.Event3Args;

import static io.neow3j.devpack.StringLiteralHelper.addressToScriptHash;

@ManifestExtra(key = "name", value = "AxLabsToken")
@ManifestExtra(key = "author", value = "AxLabs")
@SupportedStandard(neoStandard = NeoStandard.NEP_17)
@Permission(nativeContract = NativeContract.ContractManagement)
public class FungibleToken {

    static final Hash160 owner = addressToScriptHash("NM7Aky765FG8NhhwtxjXRx7jEL1cnw7PBP");

    static final int decimals = 2;
    static final byte[] totalSupplyKey = new byte[]{0x00};
    static final StorageContext ctx = Storage.getStorageContext();
    static final StorageMap assetMap = new StorageMap(ctx, new byte[]{0x01});

    // NEP-17 Methods

    @Safe
    public static String symbol() {
        return "ALT";
    }

    @Safe
    public static int decimals() {
        return decimals;
    }

    @Safe
    public static int totalSupply() {
        return Storage.getInt(ctx, totalSupplyKey);
    }

    public static boolean transfer(Hash160 from, Hash160 to, int amount, Object[] data) throws Exception {
        if (!Hash160.isValid(from) || !Hash160.isValid(to)) {
            throw new Exception("'from' or 'to' address is not a valid address.");
        }
        if (amount < 0) {
            throw new Exception("The transfer amount must be non-negative.");
        }
        if (!Runtime.checkWitness(from)) {
            throw new Exception("Invalid sender signature.");
        }
        if (!Runtime.checkWitness(from)) {
            throw new Exception("Invalid sender signature.");
        }

        if (amount > getBalance(from)) {
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
            throw new Exception("Argument is not a valid address.");
        }
        return getBalance(account);
    }

    // Events

    @DisplayName("Transfer")
    static Event3Args<Hash160, Hash160, Integer> onTransfer;

    // Deploy, Update, Destroy

    @OnDeployment
    public static void deploy(Object data, boolean update) throws Exception {
        if (!update) {
            throwIfSignerIsNotOwner();
            // Initialize the supply
            int initialSupply = 200_000_000;
            Storage.put(ctx, totalSupplyKey, initialSupply);
            // Allocate all tokens to the contract owner.
            assetMap.put(owner.toByteArray(), initialSupply);
        }
    }

    public static void update(ByteString script, String manifest) throws Exception {
        throwIfSignerIsNotOwner();
        ContractManagement.update(script, manifest);
    }

    public static void destroy() throws Exception {
        throwIfSignerIsNotOwner();
        ContractManagement.destroy();
    }

    // Custom Methods

    @Safe
    public static Hash160 contractOwner() {
        return owner;
    }

    // Private Helper Methods

    private static void throwIfSignerIsNotOwner() throws Exception {
        if (!Runtime.checkWitness(owner)) {
            throw new Exception("The calling entity is not the owner of this contract.");
        }
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

}
