package io.neow3j.examples.contractdevelopment.contracts;

import static io.neow3j.devpack.Helper.concat;
import static io.neow3j.devpack.Helper.toByteArray;
import static io.neow3j.devpack.Runtime.checkWitness;
import static io.neow3j.devpack.StringLiteralHelper.addressToScriptHash;

import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.FindOptions;
import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.Iterator;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.StorageContext;
import io.neow3j.devpack.StorageMap;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.Safe;
import io.neow3j.devpack.annotations.SupportedStandards;
import io.neow3j.devpack.contracts.ContractManagement;
import io.neow3j.devpack.events.Event3Args;
import io.neow3j.devpack.events.Event4Args;

@ManifestExtra(key = "author", value = "AxLabs")
@SupportedStandards("NEP-11")
public class NonFungibleToken {

    private static final Hash160 superAdmin =
            addressToScriptHash("NUrPrFLETzoe7N2FLi2dqTvLwc9L2Em84K");

    private static final byte prefixTotalSupply = 10;
    private static final byte prefixTokenOwner = 11;
    private static final byte prefixTokenBalance = 12;
    private static final byte prefixProperties = 13;
    private static final byte prefixTokensOf = 14;

    private static final int TOKEN_DECIMALS = 8;
    private static final int FACTOR = 100_000_000;

    @DisplayName("mint")
    private static Event3Args<Hash160, byte[], byte[]> onMint;

    @DisplayName("burn")
    private static Event3Args<Hash160, byte[], Integer> onBurn;

    @DisplayName("transfer")
    private static Event4Args<Hash160, Hash160, Integer, byte[]> onTransfer;

    @Safe
    public static String symbol() {
        return "AXL";
    }

    @Safe
    public static int decimals() {
        return TOKEN_DECIMALS;
    }

    @Safe
    public static Hash160 superAdmin() {
        return superAdmin;
    }

    @Safe
    public static int totalSupply() {
        return Storage.get(Storage.getStorageContext(), toByteArray(prefixTotalSupply)).toInteger();
    }

    private static void setTotalSupply(int total) {
        Storage.put(Storage.getStorageContext(), toByteArray(prefixTotalSupply), total);
    }

    private static byte[] createStoragePrefix(byte prefix, byte[] key) {
        return concat(toByteArray(prefix), key);
    }

    public static boolean mint(byte[] tokenId, Hash160 owner, byte[] properties)
            throws Exception {

        if (!checkWitness(superAdmin)) {
            throw new Exception("No authorization.");
        }

        if (properties.length > 2048) {
            throw new Exception("The length of properties should be less than 2048.");
        }

        StorageMap tokenOwnerMap = Storage.getStorageContext()
                .createMap(createStoragePrefix(prefixTokenOwner, tokenId));
        if (tokenOwnerMap.get(owner.toByteArray()) != null) {
            throw new Exception("This owner already has a token with the given id.");
        }

        StorageMap tokenOfMap = Storage.getStorageContext()
                .createMap(createStoragePrefix(prefixTokensOf, owner.toByteArray()));
        byte[] key = createStoragePrefix(prefixProperties, tokenId);
        Storage.put(Storage.getStorageContext(), key, properties);
        tokenOwnerMap.put(owner.toByteArray(), owner.toByteArray());
        tokenOfMap.put(tokenId, tokenId);

        int totalSupply = totalSupply();
        setTotalSupply(totalSupply + FACTOR);

        StorageMap tokenBalanceMap = Storage.getStorageContext()
                .createMap(createStoragePrefix(prefixTokenBalance, owner.toByteArray()));
        tokenBalanceMap.put(tokenId, FACTOR);

        onMint.fire(owner, tokenId, properties);
        return true;
    }

    @Safe
    public static ByteString properties(byte[] tokenId) {
        byte[] key = createStoragePrefix(prefixProperties, tokenId);
        return Storage.get(Storage.getReadOnlyContext(), key);
    }

    @Safe
    @SuppressWarnings("unchecked")
    public static int balanceOf(Hash160 owner, byte[] tokenId) {
        StorageContext context = Storage.getReadOnlyContext();
        byte[] prefix = createStoragePrefix(prefixTokenBalance, owner.toByteArray());
        if (tokenId == null) {
            Iterator<ByteString> iterator = Storage.find(
                    context,
                    prefix,
                    FindOptions.ValuesOnly);
            int result = 0;
            while (iterator.next()) {
                ByteString value = iterator.get();
                result += value.toInteger();
            }
            return result;
        }

        ByteString value = context.createMap(prefix).get(tokenId);
        return value == null ? 0 : value.toInteger();
    }

    @Safe
    @SuppressWarnings("unchecked")
    public static Iterator<ByteString> ownerOf(byte[] tokenId) {
        return Storage.find(
                Storage.getReadOnlyContext(),
                createStoragePrefix(prefixTokenOwner, tokenId),
                FindOptions.KeysOnly);
    }

    @Safe
    @SuppressWarnings("unchecked")
    public static Iterator<ByteString> tokensOf(Hash160 owner) {
        return Storage.find(
                Storage.getReadOnlyContext(),
                createStoragePrefix(prefixTokensOf, owner.toByteArray()),
                FindOptions.ValuesOnly);
    }

    public static boolean burn(byte[] tokenId, Hash160 owner, int amount) throws Exception {
        if (amount < 0 || amount > FACTOR) {
            throw new Exception("The parameter 'amount' is out of range");
        }
        if (amount == 0) {
            return true;
        }
        if (!checkWitness(owner)) {
            throw new Exception("No authorization.");
        }

        StorageContext context = Storage.getStorageContext();
        StorageMap tokenBalanceMap = context.createMap(
                createStoragePrefix(prefixTokenBalance, owner.toByteArray()));
        ByteString balanceValue = tokenBalanceMap.get(tokenId);
        int balance = balanceValue == null ? 0 : balanceValue.toInteger();
        if (balance < amount) {
            return false;
        }

        int totalSupply = totalSupply();
        balance -= amount;
        totalSupply -= amount;
        if (balance == 0) {
            tokenBalanceMap.delete(tokenId);
            StorageMap tokenOwnerMap = context.createMap(
                    createStoragePrefix(prefixTokenOwner, tokenId));
            tokenOwnerMap.delete(owner.toByteArray());
        } else {
            tokenBalanceMap.put(tokenId, balance);
        }
        setTotalSupply(totalSupply);

        onBurn.fire(owner, tokenId, amount);
        return true;
    }

    public static boolean transfer(Hash160 from, Hash160 to, int amount, byte[] tokenId)
            throws Exception {
        if (amount < 0 || amount > FACTOR) {
            throw new Exception("The parameter 'amount' is out of range.");
        }
        if (!checkWitness(from)) {
            throw new Exception("No authorization.");
        }

        if (from.equals(to)) {
            onTransfer.fire(from, to, amount, tokenId);
            return true;
        }
        StorageContext context = Storage.getStorageContext();
        StorageMap fromTokenBalanceMap = context.createMap(
                createStoragePrefix(prefixTokenBalance, from.toByteArray()));
        StorageMap toTokenBalanceMap = context.createMap(
                createStoragePrefix(prefixTokenBalance, to.toByteArray()));
        StorageMap tokenOwnerMap = context.createMap(
                createStoragePrefix(prefixTokenOwner, tokenId));
        StorageMap fromTokensOfMap = context.createMap(
                createStoragePrefix(prefixTokensOf, from.toByteArray()));
        StorageMap toTokensOfMap = context.createMap(
                createStoragePrefix(prefixTokensOf, to.toByteArray()));

        int fromTokenBalance = fromTokenBalanceMap.get(tokenId).toInteger();
        if (fromTokenBalance == 0 || fromTokenBalance < amount) {
            return false;
        }
        int fromNewBalance = fromTokenBalance - amount;
        if (fromNewBalance == 0) {
            tokenOwnerMap.delete(from.toByteArray());
            fromTokensOfMap.delete(tokenId);
        }
        fromTokenBalanceMap.put(tokenId, fromNewBalance);

        int toTokenBalance = toTokenBalanceMap.get(tokenId).toInteger();
        if (toTokenBalance == 0 && amount > 0) {
            tokenOwnerMap.put(to.toByteArray(), to.toByteArray());
            toTokenBalanceMap.put(tokenId, amount);
            toTokensOfMap.put(tokenId, tokenId);
        } else {
            toTokenBalanceMap.put(tokenId, toTokenBalance + amount);
        }

        onTransfer.fire(from, to, amount, tokenId);
        return true;
    }

    public static boolean migrate(ByteString script, String manifest) throws Exception {
        if (!checkWitness(superAdmin)) {
            throw new Exception("No authorization.");
        }
        if (script.length() == 0 || manifest.length() == 0) {
            return false;
        }
        ContractManagement.update(script, manifest);
        return true;
    }

    public static boolean destroy() throws Exception {
        if (!checkWitness(superAdmin)) {
            throw new Exception("No authorization.");
        }
        ContractManagement.destroy();
        return true;
    }

}
