package io.neow3j.examples.contractdev.contracts;

import static io.neow3j.devpack.Helper.toByteString;
import static io.neow3j.devpack.Helper.toInt;
import static io.neow3j.devpack.StringLiteralHelper.addressToScriptHash;

import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.contracts.ContractManagement;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.StorageContext;
import io.neow3j.devpack.StorageMap;

@ManifestExtra(key = "author", value = "AxLabs")
public class AxLabsToken {

    static final Hash160 contractOwner = addressToScriptHash(
            "NX8GreRFGFK5wpGMWetpX93HmtrezGogzk");

    static final StorageContext ctx = Storage.getStorageContext();

    static final StorageMap contractMap = ctx.createMap("contract");
    static final String totalSupplyKey = "totalSupply";

    static final StorageMap tokenOwnerMap = ctx.createMap("tokenOwner");
    static final StorageMap balanceMap = ctx.createMap("balance");
    static final StorageMap propertiesMap = ctx.createMap("props");

    /**
     * Gets the address of the contract owner.
     *
     * @return the address of the contract owner.
     */
    public static Hash160 contractOwner() {
        return contractOwner;
    }

    /**
     * Gets the symbol of the token.
     * 
     * @return the symbol.
     */
    public static String symbol() {
        return "ALT";
    }

    /**
     * Gets the total token supply of the contract.
     *
     * @return the total token supply.
     */
    public static int totalSupply() {
        return totalSupplyGet();
    }

    private static int totalSupplyGet() {
        return toInt(contractMap.get(totalSupplyKey));
    }

    /**
     * Gets the decimals of the token.
     * 
     * @return the decimals.
     */
    public static int decimals() {
        return 0;
    }

    /**
     * Transfers a token.
     *
     * @param from the id of the token.
     * @param to the new owner of the token.
     * @param tokenid the id of the token.
     * @return whether the transfer was successful.
     */
    public static boolean transfer(Hash160 from, Hash160 to, byte[] tokenid) {
        if (!to.isValid()) {
            return false;
        }
        // Only the token owner may transfer the token
        if (!Runtime.checkWitness(from)) {
            return false;
        }
        if (tokenOwnerGet(tokenid) != from) {
            return false;
        }
        tokenOwnerMap.put(tokenid, to.toByteArray());
        increaseBalanceOfOwner(to);
        decreaseBalanceOfOwner(from);
        return true;
    }

    private static boolean isOwner() {
        return Runtime.checkWitness(contractOwner);
    }

    /**
     * Gets the owner of the token with id {@code tokenid}.
     *
     * @param tokenid the id of the token.
     * @return the owner of the token.
     */
    public static Hash160 ownerOf(byte[] tokenid) {
        return tokenOwnerGet(tokenid);
    }

    private static Hash160 tokenOwnerGet(byte[] tokenid) {
        byte[] owner = tokenOwnerMap.get(tokenid);
        return new Hash160(owner);
    }

    private static boolean tokenIsFreeForMint(byte[] tokenid) {
        byte[] owner = tokenOwnerMap.get(tokenid);
        return owner == null;
    }

    // TODO: 26.11.20 Michael: include properties enforcing the NFT Json schema of the standard.
    /**
     * Creates a new token and assigns it to an account.
     * 
     * @param owner      the owner of the new token.
     * @param tokenid    the tokenid.
     * @return whether the token could be created.
     */
    public static boolean mintToken(Hash160 owner, byte[] tokenid) {
        if (owner.isValid() && isOwner() && tokenIsFreeForMint(tokenid)) {
            tokenOwnerMap.put(tokenid, owner.toByteArray());
            increaseTotalSupplyByOne();
            increaseBalanceOfOwner(owner);
            return true;
        }
        return false;
    }

    /**
     * Gets the number of tokens owned.
     *
     * @param owner the owner of the tokens.
     * @return the balance.
     */
    public static int balanceOf(Hash160 owner) {
        return balanceOfOwnerGet(owner);
    }

    public static String properties(byte[] tokenid) {
        return toByteString(propertiesMap.get(tokenid));
    }

    private static void increaseBalanceOfOwner(Hash160 owner) {
        balanceMap.put(owner.toByteArray(), balanceOfOwnerGet(owner) + 1);
    }

    private static void decreaseBalanceOfOwner(Hash160 owner) {
        balanceMap.put(owner.toByteArray(), balanceOfOwnerGet(owner) - 1);
    }

    private static void increaseTotalSupplyByOne() {
        contractMap.put(totalSupplyKey, totalSupplyGet() + 1);
    }

    private static int balanceOfOwnerGet(Hash160 owner) {
        if (balanceMap.get(owner.toByteArray()) == null) {
            return 0;
        }
        return toInt(balanceMap.get(owner.toByteArray()));
    }

    /**
     * Destroys the contract.
     * <p>
     * Restricted to the contract owner.
     *
     * @return whether the contract was destroyed.
     */
    public static boolean destroy() {
        if (!isOwner()) {
            return false;
        }
        ContractManagement.destroy();
        return true;
    }
}
