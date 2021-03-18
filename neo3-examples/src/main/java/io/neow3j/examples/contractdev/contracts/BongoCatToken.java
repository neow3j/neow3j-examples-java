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
import io.neow3j.devpack.annotations.OnNEP17Payment;
import io.neow3j.devpack.annotations.OnVerification;
import io.neow3j.devpack.annotations.SupportedStandards;
import io.neow3j.devpack.contracts.ContractManagement;
import io.neow3j.devpack.events.Event1Arg;
import io.neow3j.devpack.events.Event3Args;

@ManifestExtra(key = "name", value = "BongoCatToken")
@ManifestExtra(key = "author", value = "AxLabs")
@SupportedStandards("NEP-17")
public class BongoCatToken {

    static final Hash160 owner = addressToScriptHash("NUrPrFLETzoe7N2FLi2dqTvLwc9L2Em84K");

    @DisplayName("Transfer")
    static Event3Args<Hash160, Hash160, Integer> onTransfer;

    @DisplayName("onPayment")
    static Event3Args<Hash160, Integer, Object> onPayment;

    @DisplayName("onVerification")
    static Event1Arg<String> onVerification;

    static final int initialSupply = 200_000_000;
    static final String assetPrefix = "asset";
    static final String totalSupplyKey = "totalSupply";
    static final StorageContext sc = Storage.getStorageContext();
    static final StorageMap assetMap = sc.createMap(assetPrefix);

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
        if (assetGet(from) < amount) {
            return false;
        }
        if (from != to && amount != 0) {
            deductFromBalance(from, amount);
            addToBalance(to, amount);
        }

        if (ContractManagement.getContract(to) != null) {
            Contract.call(to, "onNEP17Payment", CallFlags.ALL, data);
        }

        onTransfer.notify(from, to, amount);
        return true;
    }

    public static int balanceOf(Hash160 account) throws Exception {
        if (!account.isValid()) {
            throw new Exception("Argument is not a valid address.");
        }
        return assetGet(account);
    }

    @OnDeployment
    public static void deploy(Object data, boolean update) throws Exception {
        if (!isOwner()) {
            throw new Exception("The calling entity is not the owner of this contract.");
        }
        if (getTotalSupply() > 0) {
            throw new Exception("Contract was already deployed.");
        }
        if (!update) {
            // Initialize supply
            Storage.put(sc, totalSupplyKey, getTotalSupply() + initialSupply);
            // And allocate all tokens to the contract owner.
            addToBalance(owner, initialSupply);
        }
    }

    public static void update(byte[] script, String manifest) throws Exception {
        if (!isOwner()) {
            throw new Exception("The calling entity is not the owner of this contract.");
        }
        if (script.length == 0 && manifest.length() == 0) {
            throw new Exception("The new contract script and manifest must not be empty.");
        }
        ContractManagement.update(script, manifest);
    }

    public static void destroy() throws Exception {
        if (!isOwner()) {
            throw new Exception("The calling entity is not the owner of this contract.");
        }
        ContractManagement.destroy();
    }

    @OnVerification
    public static boolean verify() throws Exception {
        if (!isOwner()) {
            throw new Exception("The calling entity is not the owner of this contract.");
        }
        onVerification.notify("It's the owner!");
        return true;
    }

    @OnNEP17Payment
    public static void onPayment(Hash160 from, int amount, Object data) {
        onPayment.notify(from, amount, data);
    }

    /**
     * Gets the address of the contract owner.
     *
     * @return the address of the contract owner.
     */
    public static Hash160 contractOwner() {
        return owner;
    }

    private static boolean isOwner() {
        return Runtime.checkWitness(owner);
    }

    private static void addToBalance(Hash160 key, int value) {
        assetMap.put(key.toByteArray(), assetGet(key) + value);
    }

    private static void deductFromBalance(Hash160 key, int value) {
        int oldValue = assetGet(key);
        if (oldValue == value) {
            assetMap.delete(key.toByteArray());
        } else {
            assetMap.put(key.toByteArray(), oldValue - value);
        }
    }

    private static int assetGet(Hash160 key) {
        return Helper.toInt(assetMap.get(key.toByteArray()));
    }

}
