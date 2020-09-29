package io.neow3j.examples.sc.contracts;

import static io.neow3j.devpack.framework.Helper.toInt;

import io.neow3j.devpack.framework.Contract;
import io.neow3j.devpack.framework.ExecutionEngine;
import io.neow3j.devpack.framework.Helper;
import io.neow3j.devpack.framework.Runtime;
import io.neow3j.devpack.framework.Storage;
import io.neow3j.devpack.framework.annotations.EntryPoint;
import io.neow3j.devpack.framework.annotations.Features;
import io.neow3j.devpack.framework.annotations.ManifestExtra;

// A NEP-5 token contract.
@ManifestExtra(key = "name", value = "BongoCatToken")
@ManifestExtra(key = "author", value = "neow3j")
@Features(hasStorage = true)
public class BongoCatToken {

    // Little-endian script hash "cc45cc8987b0e35371f5685431e3c8eeea306722"
    static byte[] owner = new byte[]{(byte) 0x22, (byte) 0x67, (byte) 0x30, (byte) 0xea,
            (byte) 0xee, (byte) 0xc8, (byte) 0xe3, (byte) 0x31, (byte) 0x54, (byte) 0x68,
            (byte) 0xf5, (byte) 0x71, (byte) 0x53, (byte) 0xe3, (byte) 0xb0, (byte) 0x87,
            (byte) 0x89, (byte) 0xcc, (byte) 0x45, (byte) 0xcc};

    static int InitialSupply = 200_000_000;

    // Storage prefixes and keys
    static String contractMapName = "contract";
    static String totalSupplyKey = "totalSupply";
    static String assetMapName = "asset";

    public static String name() {
        return "bongo";
    }

    public static String symbol() {
        return "BCT";
    }

    public static int decimals() {
        return 8;
    }

    public static int totalSupply() {
        return totalSupplyGet();
    }

    @EntryPoint
    public static boolean transfer(byte[] from, byte[] to, int amount) {
        if (!isValidAddress(from) || !isValidAddress(to)) {
            return false;
        }
        if (amount <= 0) {
            return false;
        }
        if (!Runtime.checkWitness(from) && !(from == ExecutionEngine.getCallingScriptHash())) {
            return false;
        }
        if (assetGet(from) < amount) {
            return false;
        }
        if (from == to) {
            return true;
        }
        assetReduce(from, amount);
        assetIncrease(to, amount);
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
        if (toInt(Storage.getStorageContext().createMap(contractMapName).get(totalSupplyKey)) > 0) {
            return false;
        }
        if (totalSupplyGet() > 0) {
            return false;
        }
        totalSupplyIncrease(InitialSupply);
        assetIncrease(owner, InitialSupply);
        Storage.getStorageContext().createMap(contractMapName).put(totalSupplyKey, InitialSupply);
        Storage.getStorageContext().createMap(assetMapName).put(owner, InitialSupply);
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

    private static void totalSupplyIncrease(int value) {
        totalSupplyPut(totalSupplyGet() + value);
    }

    private static void totalSupplyPut(int value) {
        Storage.getStorageContext().createMap(contractMapName).put(totalSupplyKey, value);
    }

    private static int totalSupplyGet() {
        return toInt(Storage.getStorageContext().createMap(contractMapName).get(totalSupplyKey));
    }

    private static void assetIncrease(byte[] key, int value) {
        assetPut(key, assetGet(key) + value);
    }

    private static void assetReduce(byte[] key, int value) {
        int oldValue = assetGet(key);
        if (oldValue == value) {
            assetRemove(key);
        } else {
            assetPut(key, oldValue - value);
        }
    }

    private static void assetPut(byte[] key, int value) {
        Storage.getStorageContext().createMap(assetMapName).put(key, value);
    }

    private static int assetGet(byte[] key) {
        return Helper.toInt(Storage.getStorageContext().createMap(assetMapName).get(key));
    }

    private static void assetRemove(byte[] key) {
        Storage.getStorageContext().createMap(assetMapName).delete(key);
    }
}
