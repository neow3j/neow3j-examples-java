package io.neow3j.examples.contractdev.contracts;

import static io.neow3j.devpack.StringLiteralHelper.addressToScriptHash;
import io.neow3j.devpack.CallFlags;
import io.neow3j.devpack.Contract;
import io.neow3j.devpack.ExecutionEngine;
import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.Helper;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.StorageContext;
import io.neow3j.devpack.StorageMap;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.OnDeployment;
import io.neow3j.devpack.annotations.OnVerification;
import io.neow3j.devpack.annotations.SupportedStandards;
import io.neow3j.devpack.contracts.ContractManagement;
import io.neow3j.devpack.events.Event3Args;

@ManifestExtra(key = "name", value = "FungibleToken")
@ManifestExtra(key = "author", value = "AxLabs")
@SupportedStandards("NEP-17")
public class FungibleToken {

    static final Hash160 owner = addressToScriptHash("NUrPrFLETzoe7N2FLi2dqTvLwc9L2Em84K");

    @DisplayName("Transfer")
    static Event3Args<Hash160, Hash160, Integer> onTransfer;

    static final int initialSupply = 200_000_000;
    static final int decimals = 8;
    static final String assetPrefix = "asset";
    static final String totalSupplyKey = "totalSupply";
    static final StorageContext sc = Storage.getStorageContext();
    static final StorageMap assetMap = sc.createMap(assetPrefix);

    public static String symbol() {
        return "FGT";
    }

    public static int decimals() {
        return decimals;
    }

    public static int totalSupply() {
        return getTotalSupply();
    }

    static int getTotalSupply() {
        return Helper.toInt(Storage.get(sc, totalSupplyKey));
    }

    public static boolean transfer(Hash160 from, Hash160 to, int amount, Object[] data)
            throws Exception {

        if (!from.isValid() || !to.isValid()) {
            throw new Exception("From or To address is not a valid address.");
        }
        if (amount < 0) {
            throw new Exception("The transfer amount was negative.");
        }
        if (!Runtime.checkWitness(from) && from != ExecutionEngine.getCallingScriptHash()) {
            throw new Exception("Invalid sender signature. The sender of the tokens needs to be "
                    + "the signing account.");
        }
        if (getBalance(from) < amount) {
            return false;
        }
        if (from != to && amount != 0) {
            deductFromBalance(from, amount);
            addToBalance(to, amount);
        }

        onTransfer.fire(from, to, amount);
        if (ContractManagement.getContract(to) != null) {
            Contract.call(to, "onNEP17Payment", CallFlags.ALL, data);
        }

        return true;
    }

    public static int balanceOf(Hash160 account) throws Exception {
        if (!account.isValid()) {
            throw new Exception("Argument is not a valid address.");
        }
        return getBalance(account);
    }

    @OnDeployment
    public static void deploy(Object data, boolean update) throws Exception {
        throwIfSignerIsNotOwner();
        if (!update) {
            if (getTotalSupply() > 0) {
                throw new Exception("Contract was already deployed.");
            }
            // Initialize supply
            Storage.put(sc, totalSupplyKey, initialSupply * decimals);
            // And allocate all tokens to the contract owner.
            assetMap.put(owner.toString(), initialSupply * decimals);
        }
    }

    public static void update(byte[] script, String manifest) throws Exception {
        throwIfSignerIsNotOwner();
        if (script.length == 0 && manifest.length() == 0) {
            throw new Exception("The new contract script and manifest must not be empty.");
        }
        ContractManagement.update(script, manifest);
    }

    public static void destroy() throws Exception {
        throwIfSignerIsNotOwner();
        ContractManagement.destroy();
    }

    @OnVerification
    public static boolean verify() throws Exception {
        throwIfSignerIsNotOwner();
        return true;
    }

    /**
     * Gets the address of the contract owner.
     *
     * @return the address of the contract owner.
     */
    public static Hash160 contractOwner() {
        return owner;
    }

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
        if (oldValue == value) {
            assetMap.delete(key.toByteArray());
        } else {
            assetMap.put(key.toByteArray(), oldValue - value);
        }
    }

    private static int getBalance(Hash160 key) {
        return Helper.toInt(assetMap.get(key.toByteArray()));
    }

}
