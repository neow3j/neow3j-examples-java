package io.neow3j.examples.contractdev.contracts;

import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.StorageContext;

// A name service contract that allows anyone to register a domain name under her address.
@ManifestExtra(key = "name", value = "NameService")
@ManifestExtra(key = "author", value = "neow3j")
public class NameService {

    static StorageContext ctx = Storage.getStorageContext();

    public static byte[] query(String domain) {
        return Storage.get(ctx, domain);
    }

    public static boolean register(String domain, byte[] owner) {
        if (owner == null) {
            return false;
        }
        Hash160 ownerAddrHash = new Hash160(owner);
        if (!Runtime.checkWitness(ownerAddrHash)) {
            return false;
        }
        byte[] value = Storage.get(ctx, domain);
        if (value != null) {
            return false;
        }
        Storage.put(ctx, domain, owner);
        return true;
    }

    public static boolean transfer(String domain, Hash160 to) {
        if (!Runtime.checkWitness(to)) {
            return false;
        }
        byte[] from = Storage.get(ctx, domain);
        if (from == null) {
            return false;
        }
        Hash160 fromAddrHash = new Hash160(from);
        if (!Runtime.checkWitness(fromAddrHash)) {
            return false;
        }
        Storage.put(ctx, domain, to.toByteArray());
        return true;
    }

    public static boolean delete(String domain) {
        byte[] owner = Storage.get(ctx, domain);
        if (owner == null) {
            return false;
        }
        Hash160 ownerAddrHash = new Hash160(owner);
        if (!Runtime.checkWitness(ownerAddrHash)) {
            return false;
        }
        Storage.delete(ctx, domain);
        return true;
    }
}