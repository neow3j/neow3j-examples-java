package io.neow3j.examples.contract_development.contracts;

import static io.neow3j.devpack.Helper.toByteString;
import static io.neow3j.devpack.Helper.toInt;

import io.neow3j.devpack.StringLiteralHelper;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.contracts.ManagementContract;
import io.neow3j.devpack.neo.Runtime;
import io.neow3j.devpack.neo.Storage;
import io.neow3j.devpack.neo.StorageContext;
import io.neow3j.devpack.neo.StorageMap;

@ManifestExtra(key = "name", value = "AxLabsToken")
@ManifestExtra(key = "author", value = "AxLabs")
public class AxLabsToken {

    static final byte[] contractOwner = StringLiteralHelper.addressToScriptHash(
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
    public static byte[] contractOwner() {
        return contractOwner;
    }

    // TODO: 26.11.20 Michael: move to manifest for preview4 release
    /**
     * Gets the name of the token.
     * 
     * @return the name.
     */
    public static String name() {
        return "AxLabsToken";
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
     * @param tokenid the id of the token.
     * @param to the new owner of the token.
     * @return whether the transfer was successful.
     */
    public static boolean transfer(byte[] from, byte[] to, byte[] tokenid) {
        if (!isValidAddress(to)) {
            return false;
        }
        // Only the token owner may transfer the token
        if (!Runtime.checkWitness(from)) {
            return false;
        }
        if (tokenOwnerGet(tokenid) != from) {
            return false;
        }
        tokenOwnerMap.put(tokenid, to);
        increaseBalanceOfOwner(to);
        decreaseBalanceOfOwner(from);
        return true;
    }

    private static boolean isOwner() {
        return Runtime.checkWitness(contractOwner);
    }

    /**
     * Gets the owner of the token with id {@tokenid}.
     *
     * @param tokenid the id of the token.
     * @return the owner of the token.
     */
    public static byte[] ownerOf(byte[] tokenid) {
        return tokenOwnerGet(tokenid);
    }

    private static byte[] tokenOwnerGet(byte[] tokenid) {
        return tokenOwnerMap.get(tokenid);
    }

    // TODO: 26.11.20 Michael: include properties enforcing the NFT Json schema of the standard.
    /**
     * Creates a new token and assigns it to an account.
     * 
     * @param owner      the owner of the new token.
     * @param tokenid    the tokenid.
     * @return whether the token could be created.
     */
    public static boolean mintToken(byte[] owner, byte[] tokenid) {
        if (!isValidAddress(owner) || !isOwner() || tokenOwnerGet(tokenid) != null) {
            return false;
        }
        tokenOwnerMap.put(tokenid, owner);
        increaseTotalSupplyByOne();
        increaseBalanceOfOwner(owner);
        return true;
    }

    /**
     * Gets the number of tokens owned.
     *
     * @param owner the owner of the tokens.
     * @return the balance.
     */
    public static int balanceOf(byte[] owner) {
        return balanceOfOwnerGet(owner);
    }

    public static String properties(byte[] tokenid) {
        return toByteString(propertiesMap.get(tokenid));
    }

    private static void increaseBalanceOfOwner(byte[] owner) {
        balanceMap.put(owner, balanceOfOwnerGet(owner) + 1);
    }

    private static void decreaseBalanceOfOwner(byte[] owner) {
        balanceMap.put(owner, balanceOfOwnerGet(owner) - 1);
    }

    private static void increaseTotalSupplyByOne() {
        contractMap.put(totalSupplyKey, totalSupplyGet() + 1);
    }

    private static boolean isValidAddress(byte[] address) {
        return address.length == 20 && toInt(address) != 0;
    }

    private static int balanceOfOwnerGet(byte[] owner) {
        if (balanceMap.get(owner) == null) {
            return 0;
        }
        return toInt(balanceMap.get(owner));
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
        ManagementContract.destroy();
        return true;
    }
}
