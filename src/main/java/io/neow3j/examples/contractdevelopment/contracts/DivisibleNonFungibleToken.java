package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.Contract;
import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.Helper;
import io.neow3j.devpack.Iterator;
import io.neow3j.devpack.Map;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.StorageContext;
import io.neow3j.devpack.StorageMap;
import io.neow3j.devpack.StringLiteralHelper;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.OnDeployment;
import io.neow3j.devpack.annotations.Permission;
import io.neow3j.devpack.annotations.Safe;
import io.neow3j.devpack.annotations.SupportedStandard;
import io.neow3j.devpack.constants.CallFlags;
import io.neow3j.devpack.constants.FindOptions;
import io.neow3j.devpack.constants.NativeContract;
import io.neow3j.devpack.constants.NeoStandard;
import io.neow3j.devpack.contracts.ContractManagement;
import io.neow3j.devpack.events.Event2Args;
import io.neow3j.devpack.events.Event3Args;
import io.neow3j.devpack.events.Event4Args;

@DisplayName("SharedFurryFriends")
@ManifestExtra(key = "author", value = "AxLabs")
@SupportedStandard(neoStandard = NeoStandard.NEP_11)
@Permission(nativeContract = NativeContract.ContractManagement)
public class DivisibleNonFungibleToken {

    static final StorageContext ctx = Storage.getStorageContext();
    static final StorageMap contractMap = new StorageMap(ctx, 1);
    static final String totalSupplyKey = "totSupply";

    static final StorageMap registryMap = new StorageMap(ctx, 8);

    // region keys of key-value pairs in NFT properties
    static final String propName = "name";
    static final String propDescription = "description";
    static final String propImage = "image";
    static final String propTokenURI = "tokenURI";
    static final StorageMap propNameMap = new StorageMap(ctx, 12);
    static final StorageMap propDescriptionMap = new StorageMap(ctx, 13);
    static final StorageMap propImageMap = new StorageMap(ctx, 14);
    static final StorageMap propTokenURIMap = new StorageMap(ctx, 15);
    static final StorageMap amountOfOwnedTokensMap = new StorageMap(ctx, 21);
    static final StorageMap totalBalanceMap = new StorageMap(ctx, 22);

    static final ByteString balanceTokenOwnerKey = new ByteString("balanceTokenOwner");
    static final ByteString ownerOfKey = new ByteString("ownerOf");
    static final ByteString tokensOfKey = new ByteString("tokensOf");

    // endregion keys of key-value pairs in NFT properties
    // region NFT constants

    // This NFT has 8 decimals, hence, 100_000_000 fractions are equivalent to a full NFT.
    private static final int FACTOR = 100_000_000;

    // endregion NFT constants
    // region deploy, update, destroy

    @OnDeployment
    public static void deploy(Object data, boolean update) {
        if (!Runtime.checkWitness(contractOwner())) {
            error.fire("No authorization", "deploy");
            Helper.abort();
        }
        if (!update) {
            contractMap.put(totalSupplyKey, 0);
        }
    }

    public static void update(ByteString script, String manifest) {
        if (!Runtime.checkWitness(contractOwner())) {
            error.fire("No authorization", "update");
            Helper.abort();
        }
        new ContractManagement().update(script, manifest);
    }

    public static void destroy() {
        if (!Runtime.checkWitness(contractOwner())) {
            error.fire("No authorization", "destroy");
            Helper.abort();
        }
        new ContractManagement().destroy();
    }

    // endregion deploy
    // region NEP-11 methods

    @Safe
    public static String symbol() {
        return "NEOW";
    }

    @Safe
    public static int decimals() {
        return 8;
    }

    @Safe
    public static int totalSupply() {
        return contractMap.getInt(totalSupplyKey);
    }

    @Safe
    public static int balanceOf(Hash160 owner) throws Exception {
        if (!Hash160.isValid(owner)) {
            throw new Exception("The parameter 'owner' must be a 20-byte address.");
        }
        return amountOfOwnedTokensMap.getIntOrZero(owner.toByteArray());
    }

    @Safe
    public static Iterator<ByteString> tokensOf(Hash160 owner) throws Exception {
        if (!Hash160.isValid(owner)) {
            throw new Exception("The parameter 'owner' must be a 20-byte address.");
        }
        return (Iterator<ByteString>) Storage.find(ctx.asReadOnly(), createTokensOfPrefix(owner),
                FindOptions.ValuesOnly);
    }

    public static boolean transfer(Hash160 to, ByteString tokenId, Object data) throws Exception {
        if (!Hash160.isValid(to)) {
            throw new Exception("The parameter 'to' must be a 20-byte address.");
        }
        if (tokenId.length() > 64) {
            throw new Exception("The parameter 'tokenId' must be a valid NFT ID (64 or less bytes long).");
        }
        Iterator<ByteString> owners = ownerOf(tokenId);
        Hash160 from = new Hash160(owners.get());
        if (owners.next()) {
            return false;
        }
        if (!Runtime.checkWitness(from)) {
            return false;
        }
        assert balanceOf(from, tokenId) == FACTOR;
        onTransfer.fire(from, to, FACTOR, tokenId);
        if (from != to) {
            int amountOfOwnedtokens = amountOfOwnedTokensMap.getInt(from.toByteArray());
            if (amountOfOwnedtokens == 1) {
                amountOfOwnedTokensMap.delete(from.toByteArray());
            } else {
                amountOfOwnedTokensMap.put(from.toByteArray(), amountOfOwnedtokens - 1);
            }
            removeTokenOwner(tokenId, from);
            removeOwnersToken(from, tokenId);
            decreaseBalance(from, tokenId, FACTOR);
            decreaseTotalBalance(from, FACTOR);

            addTokenOwner(tokenId, to);
            addOwnersTokens(to, tokenId);
            increaseBalance(to, tokenId, FACTOR);
            increaseTotalOwnerBalance(to, FACTOR);
        }
        onTransfer.fire(from, to, FACTOR, tokenId);
        if (new ContractManagement().getContract(to) != null) {
            Contract.call(to, "onNEP11Payment", CallFlags.All, new Object[]{from, FACTOR, tokenId, data});
        }
        return true;
    }

    // endregion NEP-11 methods
    // region divisible NEP-11 methods

    public static boolean transfer(Hash160 from, Hash160 to, int amount, ByteString tokenId, Object data)
            throws Exception {

        if (!Hash160.isValid(to)) {
            throw new Exception("The parameter 'to' must be a 20-byte address.");
        }
        if (tokenId.length() > 64) {
            throw new Exception("The parameter 'tokenId' must be a valid NFT ID (64 or less bytes long).");
        }
        if (amount < 0 || amount > FACTOR) {
            throw new Exception("The 'amount' must be between 0 and 100_000_000.");
        }
        int balance = balanceOf(from, tokenId);
        if (balance < amount) {
            return false;
        }
        if (!Runtime.checkWitness(from)) {
            return false;
        }
        if (amount == balance) {
            int amountOfOwnedTokens = amountOfOwnedTokensMap.getInt(from.toByteArray());
            if (amountOfOwnedTokens == 1) {
                amountOfOwnedTokensMap.delete(from.toByteArray());
            } else {
                amountOfOwnedTokensMap.put(from.toByteArray(), amountOfOwnedTokens - 1);
            }
            removeTokenOwner(tokenId, from);
            removeOwnersToken(from, tokenId);
        }
        decreaseBalance(from, tokenId, amount);
        decreaseTotalBalance(from, amount);

        addTokenOwner(tokenId, to);
        addOwnersTokens(to, tokenId);
        increaseBalance(to, tokenId, amount);
        increaseTotalOwnerBalance(to, amount);
        onTransfer.fire(from, to, amount, tokenId);
        if (new ContractManagement().getContract(to) != null) {
            Contract.call(to, "onNEP11Payment", CallFlags.All, new Object[]{from, amount, tokenId, data});
        }
        return true;
    }

    @Safe
    public static Iterator<ByteString> ownerOf(ByteString tokenId) throws Exception {
        if (tokenId.length() > 64) {
            throw new Exception("The parameter 'tokenId' must be a valid NFT ID (64 or less bytes long).");
        }
        return (Iterator<ByteString>) Storage.find(ctx.asReadOnly(), createOwnerOfPrefix(tokenId),
                FindOptions.ValuesOnly);
    }

    @Safe
    public static int balanceOf(Hash160 owner, ByteString tokenId) {
        return new StorageMap(ctx, createTokensOfPrefix(owner)).getIntOrZero(tokenId);
    }

    // endregion divisible NEP-11 methods
    // region optional methods

    @Safe
    public static Iterator<ByteString> tokens() {
        return (Iterator<ByteString>) registryMap.find(FindOptions.ValuesOnly);
    }

    @Safe
    public static Map<String, String> properties(ByteString tokenId) throws Exception {
        Map<String, String> p = new Map<>();
        ByteString tokenName = propNameMap.get(tokenId);
        if (tokenName == null) {
            throw new Exception("This token id does not exist.");
        }

        p.put(propName, tokenName.toString());
        ByteString tokenDescription = propDescriptionMap.get(tokenId);
        if (tokenDescription != null) {
            p.put(propDescription, tokenDescription.toString());
        }
        ByteString tokenImage = propImageMap.get(tokenId);
        if (tokenImage != null) {
            p.put(propImage, tokenImage.toString());
        }
        ByteString tokenURI = propTokenURIMap.get(tokenId);
        if (tokenURI != null) {
            p.put(propTokenURI, tokenURI.toString());
        }
        return p;
    }

    // endregion optional methods
    // region events

    @DisplayName("Mint")
    private static Event3Args<Hash160, ByteString, Map<String, String>> onMint;

    @DisplayName("Transfer")
    private static Event4Args<Hash160, Hash160, Integer, ByteString> onTransfer;

    /**
     * This event is intended to be fired before aborting the VM. The first argument should be a message and the
     * second argument should be the method name whithin which it has been fired.
     */
    @DisplayName("Error")
    private static Event2Args<String, String> error;

    // endregion events
    // region custom methods

    @Safe
    public static Hash160 contractOwner() {
        return StringLiteralHelper.addressToScriptHash("NM7Aky765FG8NhhwtxjXRx7jEL1cnw7PBP");
    }

    public static boolean mint(Hash160 owner, ByteString tokenId, Map<String, String> properties) {
        if (!Runtime.checkWitness(contractOwner())) {
            fireErrorAndAbort("No authorization.", "mint");
        }
        if (registryMap.get(tokenId) != null) {
            fireErrorAndAbort("This token id already exists.", "mint");
        }

        String tokenName = properties.get(propName);
        if (tokenName == null) {
            fireErrorAndAbort("The properties must contain a value for the key `name`.", "mint");
        }
        propNameMap.put(tokenId, tokenName);
        String description = properties.get(propDescription);
        if (description != null) {
            propDescriptionMap.put(tokenId, description);
        }
        String image = properties.get(propImage);
        if (image != null) {
            propImageMap.put(tokenId, image);
        }
        String tokenURI = properties.get(propTokenURI);
        if (tokenURI != null) {
            propTokenURIMap.put(tokenId, tokenURI);
        }

        registryMap.put(tokenId, tokenId);
        increaseTotalSupply();
        addTokenOwner(tokenId, owner);
        addOwnersTokens(owner, tokenId);

        increaseTotalOwnerBalance(owner, FACTOR);
        increaseBalance(owner, tokenId, FACTOR);
        int amountOfOwnedTokens = amountOfOwnedTokensMap.getInt(owner.toByteArray());
        amountOfOwnedTokensMap.put(owner.toByteArray(), amountOfOwnedTokens + 1);
        onMint.fire(owner, tokenId, properties);
        return true;
    }

    // endregion custom methods
    // region private helper methods

    private static void fireErrorAndAbort(String msg, String method) {
        error.fire(msg, method);
        Helper.abort();
    }

    private static ByteString createOwnerOfPrefix(ByteString tokenId) {
        return ownerOfKey.concat(tokenId);
    }

    private static ByteString createTokensOfPrefix(Hash160 owner) {
        return tokensOfKey.concat(owner.toByteString());
    }

    private static ByteString createBalancePrefix(Hash160 owner) {
        return balanceTokenOwnerKey.concat(owner.toByteString());
    }

    private static void addTokenOwner(ByteString tokenId, Hash160 owner) {
        new StorageMap(ctx, createOwnerOfPrefix(tokenId)).put(owner.toByteArray(), owner.toByteArray());
    }

    private static void removeTokenOwner(ByteString tokenId, Hash160 owner) {
        new StorageMap(ctx, createOwnerOfPrefix(tokenId)).delete(owner.toByteArray());
    }

    private static void addOwnersTokens(Hash160 owner, ByteString tokenId) {
        new StorageMap(ctx, createTokensOfPrefix(owner)).put(tokenId, tokenId);
    }

    private static void removeOwnersToken(Hash160 owner, ByteString tokenId) {
        new StorageMap(ctx, createTokensOfPrefix(owner)).delete(tokenId);
    }

    private static void increaseTotalSupply() {
        int totalSupply = totalSupply();
        contractMap.put(totalSupplyKey, totalSupply + FACTOR);
    }

    private static void increaseTotalOwnerBalance(Hash160 owner, int addend) {
        ByteString totalBalance = totalBalanceMap.get(owner.toByteArray());
        if (totalBalance == null) {
            totalBalanceMap.put(owner.toByteArray(), addend);
        } else {
            totalBalanceMap.put(owner.toByteArray(), totalBalance.toInt() + addend);
        }
    }

    private static void decreaseTotalBalance(Hash160 owner, int subtrahend) {
        ByteString totalBalance = totalBalanceMap.get(owner.toByteArray());
        totalBalanceMap.put(owner.toByteArray(), totalBalance.toInt() - subtrahend);
    }

    private static void increaseBalance(Hash160 owner, ByteString tokenId, int addend) {
        new StorageMap(ctx, createBalancePrefix(owner)).put(tokenId, getBalanceOf(owner, tokenId) + addend);
    }

    private static void decreaseBalance(Hash160 owner, ByteString tokenId, int subtrahend) {
        new StorageMap(ctx, createBalancePrefix(owner)).put(tokenId, getBalanceOf(owner, tokenId) - subtrahend);
    }

    private static int getBalanceOf(Hash160 owner, ByteString tokenId) {
        ByteString balance = new StorageMap(ctx, createBalancePrefix(owner)).get(tokenId);
        if (balance == null) {
            return 0;
        }
        return balance.toInt();
    }

    // endregion private helper methods

}
