package io.neow3j.examples.contractdev.contracts;

import static io.neow3j.devpack.StringLiteralHelper.addressToScriptHash;

import io.neow3j.devpack.Helper;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.OnDeployment;
import io.neow3j.devpack.annotations.SupportedStandards;
import io.neow3j.devpack.contracts.ManagementContract;
import io.neow3j.devpack.events.Event3Args;
import io.neow3j.devpack.neo.Runtime;
import io.neow3j.devpack.neo.Storage;
import io.neow3j.devpack.neo.StorageContext;
import io.neow3j.devpack.neo.StorageMap;
import io.neow3j.devpack.system.ExecutionEngine;

@ManifestExtra(key = "name", value = "BongoCatToken")
@ManifestExtra(key = "author", value = "AxLabs")
@SupportedStandards("NEP-17")
public class BongoCatToken {

    static final byte[] owner = addressToScriptHash("NZNos2WqTbu5oCgyfss9kUJgBXJqhuYAaj");

    @DisplayName("transfer")
    static Event3Args<byte[], byte[], Integer> onTransfer;

    static final int initialSupply = 200_000_000;
    static final String assetPrefix = "asset";
    static final String totalSupplyKey = "totalSupply";
    static final StorageContext sc = Storage.getStorageContext();
    static final StorageMap assetMap = sc.createMap(assetPrefix);

    public static String name() {
        return "Bongo";
    }

    public static String symbol() {
        return "BCT";
    }

    public static int decimals() {
        return 8;
    }

    public static int totalSupply() {
        return getTotalSupply();
    }

    static int getTotalSupply() {
        return Helper.toInt(Storage.get(sc, totalSupplyKey));
    }

    public static boolean transfer(byte[] from, byte[] to, int amount) throws Exception {
        if (!isValidAddress(from) || !isValidAddress(to)) {
            throw new Exception("From or To address is not a valid address.");
        }
        if (amount < 0) {
            throw new Exception("The transfer amount was negative.");
        }
        if (!Runtime.checkWitness(from) && from != ExecutionEngine.getCallingScriptHash()) {
            throw new Exception("Invalid sender signature. The sender of the tokens needs to be "
                    + "the signing account.");
        }
        if (assetGet(from) < amount) {
            return false;
        }
        if (from != to && amount != 0) {
            deductFromBalance(from, amount);
            addToBalance(to, amount);
        }
        onTransfer.notify(from, to, amount);
        return true;
    }

    public static int balanceOf(byte[] account) throws Exception {
        if (!isValidAddress(account)) {
            throw new Exception("Argument is not a valid address.");
        }
        // TODO: Check if this returns 0 for addresses that don't have an entry in the storage.
        return assetGet(account);
    }

    @OnDeployment
    public static void deploy(boolean update) throws Exception {
        if (update) {
            return;
        }
        if (!isOwner()) {
            throw new Exception("The calling entity is not the owner of this contract.");
        }
        if (getTotalSupply() > 0) {
            throw new Exception("Contract was already deployed.");
        }
        // Initialize supply
        Storage.put(sc, totalSupplyKey, getTotalSupply() + initialSupply);
        // And allocate all tokens to the contract owner.
        addToBalance(owner, initialSupply);
    }

    public static void update(byte[] script, String manifest) throws Exception {
        if (!isOwner()) {
            throw new Exception("The calling entity is not the owner of this contract.");
        }
        if (script.length == 0 && manifest.length() == 0) {
            throw new Exception("The new contract script and manifest must not be empty.");
        }
        ManagementContract.update(script, manifest);
    }

    public static void destroy() throws Exception {
        if (!isOwner()) {
            throw new Exception("The calling entity is not the owner of this contract.");
        }
        ManagementContract.destroy();
    }

    private static boolean isOwner() {
        return Runtime.checkWitness(owner);
    }

    private static boolean isValidAddress(byte[] address) {
        return address.length == 20 && Helper.toInt(address) != 0;
    }

    private static void addToBalance(byte[] key, int value) {
        assetMap.put(key, assetGet(key) + value);
    }

    private static void deductFromBalance(byte[] key, int value) {
        int oldValue = assetGet(key);
        if (oldValue == value) {
            assetMap.delete(key);
        } else {
            assetMap.put(key, oldValue - value);
        }
    }

    private static int assetGet(byte[] key) {
        return Helper.toInt(assetMap.get(key));
    }

}
