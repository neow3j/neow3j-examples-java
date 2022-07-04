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

@DisplayName("FurryFriends")
@ManifestExtra(key = "author", value = "AxLabs")
@SupportedStandard(neoStandard = NeoStandard.NEP_11)
@Permission(nativeContract = NativeContract.ContractManagement)
public class NonFungibleToken {

    static final StorageContext ctx = Storage.getStorageContext();
    static final StorageMap contractMap = new StorageMap(ctx, 0);
    static final StorageMap registryMap = new StorageMap(ctx, 1);
    static final StorageMap ownerOfMap = new StorageMap(ctx, 2);
    static final StorageMap balanceMap = new StorageMap(ctx, 3);

    // region keys of key-value pairs in NFT properties
    static final String propName = "name";
    static final String propDescription = "description";
    static final String propImage = "image";
    static final String propTokenURI = "tokenURI";

    static final StorageMap propNameMap = new StorageMap(ctx, 8);
    static final StorageMap propDescriptionMap = new StorageMap(ctx, 9);
    static final StorageMap propImageMap = new StorageMap(ctx, 10);
    static final StorageMap propTokenURIMap = new StorageMap(ctx, 11);

    static final byte[] totalSupplyKey = new byte[]{0x10};
    static final byte[] tokensOfKey = new byte[]{0x11};

    static final byte[] contractOwnerKey = new byte[]{0x7f};

    // endregion keys of key-value pairs in NFT properties
    // region deploy, update, destroy

    @OnDeployment
    public static void deploy(Object data, boolean update) {
        if (!update) {
            initializeContract((Hash160) data);
        }
        if (!Runtime.checkWitness(contractOwner())) {
            error.fire("No authorization", "deploy");
            Helper.abort();
        }
    }

    private static void initializeContract(Hash160 contractOwner) {
        contractMap.put(totalSupplyKey, 0);
        contractMap.put(contractOwnerKey, contractOwner);
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

    // endregion deploy, update, destroy
    // region NEP-11 methods

    @Safe
    public static String symbol() {
        return "NEOW";
    }

    @Safe
    public static int decimals() {
        return 0;
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
        return getBalance(owner);
    }

    @Safe
    public static Iterator<ByteString> tokensOf(Hash160 owner) throws Exception {
        if (!Hash160.isValid(owner)) {
            throw new Exception("The parameter 'owner' must be a 20-byte address.");
        }
        return (Iterator<ByteString>) Storage.find(ctx.asReadOnly(), createTokensOfPrefix(owner),
                (byte) (FindOptions.KeysOnly | FindOptions.RemovePrefix));
    }

    public static boolean transfer(Hash160 to, ByteString tokenId, Object data) throws Exception {
        if (!Hash160.isValid(to)) {
            throw new Exception("The parameter 'to' must be a 20-byte address.");
        }
        if (tokenId.length() > 64) {
            throw new Exception("The parameter 'tokenId' must be a valid NFT ID (64 or less bytes long).");
        }
        Hash160 owner = ownerOf(tokenId);
        if (!Runtime.checkWitness(owner)) {
            return false;
        }
        onTransfer.fire(owner, to, 1, tokenId);
        if (owner != to) {
            ownerOfMap.put(tokenId, to.toByteArray());

            new StorageMap(ctx, createTokensOfPrefix(owner)).delete(tokenId);
            new StorageMap(ctx, createTokensOfPrefix(to)).put(tokenId, 1);

            decreaseBalanceByOne(owner);
            increaseBalanceByOne(to);
        }
        if (new ContractManagement().getContract(to) != null) {
            Contract.call(to, "onNEP11Payment", CallFlags.All, new Object[]{owner, 1, tokenId, data});
        }
        return true;
    }

    // endregion NEP-11 methods
    // region non-divisible NEP-11 methods

    @Safe
    public static Hash160 ownerOf(ByteString tokenId) throws Exception {
        if (tokenId.length() > 64) {
            throw new Exception("The parameter 'tokenId' must be a valid NFT ID (64 or less bytes long).");
        }
        ByteString owner = ownerOfMap.get(tokenId);
        if (owner == null) {
            throw new Exception("This token id does not exist.");
        }
        return new Hash160(owner);
    }

    // endregion non-divisible NEP-11 methods
    // region optional NEP-11 methods

    @Safe
    public static Iterator<Iterator.Struct<ByteString, ByteString>> tokens() {
        return (Iterator<Iterator.Struct<ByteString, ByteString>>) registryMap.find(FindOptions.RemovePrefix);
    }

    @Safe
    public static Map<String, String> properties(ByteString tokenId) throws Exception {
        if (tokenId.length() > 64) {
            throw new Exception("The parameter 'tokenId' must be a valid NFT ID (64 or less bytes long).");
        }
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

    // endregion optional NEP-11 methods
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
        return contractMap.getHash160(contractOwnerKey);
    }

    public static void mint(Hash160 owner, ByteString tokenId, Map<String, String> properties) {
        if (!Runtime.checkWitness(contractOwner())) {
            fireErrorAndAbort("No authorization.", "mint");
        }
        if (registryMap.get(tokenId) != null) {
            fireErrorAndAbort("This token id already exists.", "mint");
        }
        if (!properties.containsKey(propName)) {
            fireErrorAndAbort("The properties must contain a value for the key 'name'.", "mint");
        }
        String tokenName = properties.get(propName);
        propNameMap.put(tokenId, tokenName);
        if (properties.containsKey(propDescription)) {
            String description = properties.get(propDescription);
            propDescriptionMap.put(tokenId, description);
        }
        if (properties.containsKey(propImage)) {
            String image = properties.get(propImage);
            propImageMap.put(tokenId, image);
        }
        if (properties.containsKey(propTokenURI)) {
            String tokenURI = properties.get(propTokenURI);
            propTokenURIMap.put(tokenId, tokenURI);
        }

        registryMap.put(tokenId, tokenId);
        ownerOfMap.put(tokenId, owner.toByteArray());
        new StorageMap(ctx, createTokensOfPrefix(owner)).put(tokenId, 1);

        increaseBalanceByOne(owner);
        incrementTotalSupplyByOne();
        onMint.fire(owner, tokenId, properties);
    }

    public static void burn(ByteString tokenId) {
        Hash160 owner = null;
        try {
            owner = ownerOf(tokenId);
        } catch (Exception e) {
            fireErrorAndAbort(e.getMessage(), "burn");
        }
        if (!Runtime.checkWitness(owner)) {
            fireErrorAndAbort("No authorization.", "abortIfInvalidWitness");
        }

        registryMap.delete(tokenId);
        propNameMap.delete(tokenId);
        propDescriptionMap.delete(tokenId);
        propImageMap.delete(tokenId);
        propTokenURIMap.delete(tokenId);
        ownerOfMap.delete(tokenId);

        new StorageMap(ctx, createTokensOfPrefix(owner)).delete(tokenId);
        decreaseBalanceByOne(owner);
        decrementTotalSupplyByOne();
        onTransfer.fire(owner, null, 1, tokenId);
    }

    // endregion custom methods
    // region private helper methods

    private static int getBalance(Hash160 owner) {
        return balanceMap.getIntOrZero(owner.toByteArray());
    }

    private static void fireErrorAndAbort(String msg, String method) {
        error.fire(msg, method);
        Helper.abort();
    }

    private static void increaseBalanceByOne(Hash160 owner) {
        balanceMap.put(owner.toByteArray(), getBalance(owner) + 1);
    }

    private static void decreaseBalanceByOne(Hash160 owner) {
        balanceMap.put(owner.toByteArray(), getBalance(owner) - 1);
    }

    private static void incrementTotalSupplyByOne() {
        int updatedTotalSupply = contractMap.getInt(totalSupplyKey) + 1;
        contractMap.put(totalSupplyKey, updatedTotalSupply);
    }

    private static void decrementTotalSupplyByOne() {
        int updatedTotalSupply = contractMap.getInt(totalSupplyKey) - 1;
        contractMap.put(totalSupplyKey, updatedTotalSupply);
    }

    private static byte[] createTokensOfPrefix(Hash160 owner) {
        return Helper.concat(tokensOfKey, owner.toByteArray());
    }

    // endregion private helper methods

}
