package io.neow3j.examples.contractdev.contracts;

import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.Storage;

// A name service contract that allows anyone to register a domain name under her address.
@ManifestExtra(key = "name", value = "NameService")
@ManifestExtra(key = "author", value = "neow3j")
public class NameService {

    public static byte[] query(String domain) {
        return Storage.get(domain);
    }

    public static boolean register(String domain, byte[] owner) {
        if (owner == null) {
            return false;
        }
        Hash160 ownerAddrHash = new Hash160(owner);
        if (!Runtime.checkWitness(ownerAddrHash)) {
            return false;
        }
        byte[] value = Storage.get(domain);
        if (value != null) {
            return false;
        }
        Storage.put(domain, owner);
        return true;
    }

    public static boolean transfer(String domain, Hash160 to) {
        if (!Runtime.checkWitness(to)) {
            return false;
        }
        byte[] from = Storage.get(domain);
        if (from == null) {
            return false;
        }
        Hash160 fromAddrHash = new Hash160(from);
        if (!Runtime.checkWitness(fromAddrHash)) {
            return false;
        }
        Storage.put(domain, to.toByteArray());
        return true;
    }

    public static boolean delete(String domain) {
        byte[] owner = Storage.get(domain);
        if (owner == null) {
            return false;
        }
        Hash160 ownerAddrHash = new Hash160(owner);
        if (!Runtime.checkWitness(ownerAddrHash)) {
            return false;
        }
        Storage.delete(domain);
        return true;
    }
}
