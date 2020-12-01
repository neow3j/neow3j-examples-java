package io.neow3j.examples.contract_development.contracts;

import io.neow3j.devpack.Helper;
import io.neow3j.devpack.StringLiteralHelper;
import io.neow3j.devpack.annotations.Features;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.SupportedStandards;
import io.neow3j.devpack.neo.Contract;
import io.neow3j.devpack.neo.Runtime;
import io.neow3j.devpack.neo.Storage;
import io.neow3j.devpack.neo.StorageContext;
import io.neow3j.devpack.neo.StorageMap;
import io.neow3j.devpack.system.ExecutionEngine;

@ManifestExtra(key = "name", value = "BongoCatToken")
@ManifestExtra(key = "author", value = "AxLabs")
@Features(hasStorage = true, payable = false)
@SupportedStandards("NEP-5")
public class BongoCatToken {

    static final byte[] owner = StringLiteralHelper.addressToScriptHash(
            "NZNos2WqTbu5oCgyfss9kUJgBXJqhuYAaj");

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

    public static boolean transfer(byte[] from, byte[] to, int amount) {
        if (from == to) {
            return true;
        }
        if (!isValidAddress(from) || !isValidAddress(to)) {
            return false;
        }
        if (amount <= 0) {
            return false;
        }
        if (!Runtime.checkWitness(from) && from != ExecutionEngine.getCallingScriptHash()) {
            return false;
        }
        if (assetGet(from) < amount) {
            return false;
        }
        deductFromBalance(from, amount);
        addToBalance(to, amount);
        return true;
    }

    public static int balanceOf(byte[] account) {
        if (!isValidAddress(account)) {
            return 0;
        }
        return assetGet(account);
    }

    public static boolean deploy() {
        if (!isOwner()) {
            return false;
        }
        if (getTotalSupply() > 0) {
            return false;
        }
        // Initialize supply
        Storage.put(sc, totalSupplyKey, getTotalSupply() + initialSupply);
        // And allocate all tokens to the contract owner.
        addToBalance(owner, initialSupply);
        return true;
    }

    public static boolean update(byte[] script, String manifest) {
        if (!isOwner()) {
            return false;
        }
        if (script.length == 0 && manifest.length() == 0) {
            return false;
        }
        Contract.update(script, manifest);
        return true;
    }

    public static boolean destroy() {
        if (!isOwner()) {
            return false;
        }
        Contract.destroy();
        return true;
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
