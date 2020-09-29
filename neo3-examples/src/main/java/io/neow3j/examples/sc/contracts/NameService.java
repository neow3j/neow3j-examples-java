package io.neow3j.examples.sc.contracts;

import io.neow3j.devpack.framework.Runtime;
import io.neow3j.devpack.framework.Storage;
import io.neow3j.devpack.framework.annotations.EntryPoint;
import io.neow3j.devpack.framework.annotations.Features;
import io.neow3j.devpack.framework.annotations.ManifestExtra;

// A name service contract that allows anyone to register a domain name under her address.
@ManifestExtra(key = "name", value = "NameService")
@ManifestExtra(key = "author", value = "neow3j")
@Features(hasStorage = true)
public class NameService {

    @EntryPoint
    public static byte[] query(String domain) {
        return Storage.get(domain);
    }

    public static boolean register(String domain, byte[] owner) {
        if (!Runtime.checkWitness(owner)) {
            return false;
        }
        byte[] value = Storage.get(domain);
        if (value != null) {
            return false;
        }
        Storage.put(domain, owner);
        return true;
    }

    public static boolean transfer(String domain, byte[] to) {
        if (!Runtime.checkWitness(to)) {
            return false;
        }
        byte[] from = Storage.get(domain);
        if (from == null) {
            return false;
        }
        if (!Runtime.checkWitness(from)) {
            return false;
        }
        Storage.put(domain, to);
        return true;
    }

    public static boolean delete(String domain) {
        byte[] owner = Storage.get(domain);
        if (owner == null) {
            return false;
        }
        if (!Runtime.checkWitness(owner)) {
            return false;
        }
        Storage.delete(domain);
        return true;
    }
}

