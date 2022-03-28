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
import io.neow3j.devpack.annotations.Safe;
import io.neow3j.devpack.annotations.SupportedStandard;
import io.neow3j.devpack.constants.CallFlags;
import io.neow3j.devpack.constants.FindOptions;
import io.neow3j.devpack.constants.NeoStandard;
import io.neow3j.devpack.contracts.ContractManagement;
import io.neow3j.devpack.events.Event3Args;
import io.neow3j.devpack.events.Event4Args;

@DisplayName("FurryFriends")
@ManifestExtra(key = "author", value = "AxLabs")
@SupportedStandard(neoStandard = NeoStandard.NEP_11)
public class NonFungibleToken {

    static final Hash160 contractOwner = StringLiteralHelper.addressToScriptHash("NM7Aky765FG8NhhwtxjXRx7jEL1cnw7PBP");

    static final StorageContext ctx = Storage.getStorageContext();
    static final StorageMap contractMap = new StorageMap(ctx, 0);
    static final StorageMap registryMap = new StorageMap(ctx, 1);
    static final StorageMap ownerOfMap = new StorageMap(ctx, 2);
    static final StorageMap balanceMap = new StorageMap(ctx, 3);

    // Keys of key-value pairs in NFT properties
    static final String propName = "name";
    static final String propDescription = "description";
    static final String propImage = "image";
    static final String propTokenURI = "tokenURI";

    static final StorageMap propertiesNameMap = new StorageMap(ctx, 8);
    static final StorageMap propertiesDescriptionMap = new StorageMap(ctx, 9);
    static final StorageMap propertiesImageMap = new StorageMap(ctx, 10);
    static final StorageMap propertiesTokenURIMap = new StorageMap(ctx, 11);

    static final byte[] totalSupplyKey = new byte[]{0x10};
    static final byte[] tokensOfKey = new byte[]{0x11};

    // NEP-11 Methods

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
    public static int balanceOf(Hash160 owner) {
        return balanceMap.getIntOrZero(owner.toByteArray());
    }

    @Safe
    public static Iterator<ByteString> tokensOf(Hash160 owner) {
        return (Iterator<ByteString>) Storage.find(
                ctx.asReadOnly(), createTokensOfPrefix(owner), FindOptions.RemovePrefix);
    }

    public static boolean transfer(Hash160 to, ByteString tokenId, Object[] data) {
        Hash160 owner = ownerOf(tokenId);
        assert owner != null : "This token id does not exist.";
        throwIfSignerIsNotOwner(owner);

        ownerOfMap.put(tokenId, to.toByteArray());

        new StorageMap(ctx, createTokensOfPrefix(owner)).delete(tokenId);
        new StorageMap(ctx, createTokensOfPrefix(to)).put(tokenId, 1);

        decreaseBalanceByOne(owner);
        increaseBalanceByOne(to);

        onTransfer.fire(owner, to, 1, tokenId);
        if (ContractManagement.getContract(to) != null) {
            Contract.call(to, "onNEP17Payment", CallFlags.All, data);
        }
        return true;
    }

    @Safe
    public static Hash160 ownerOf(ByteString tokenId) {
        ByteString owner = ownerOfMap.get(tokenId);
        if (owner == null) {
            return null;
        }
        return new Hash160(owner);
    }

    @Safe
    public static Iterator<Iterator.Struct<ByteString, ByteString>> tokens() {
        return (Iterator<Iterator.Struct<ByteString, ByteString>>) registryMap.find(FindOptions.RemovePrefix);
    }

    @Safe
    public static Map<String, String> properties(ByteString tokenId) {
        Map<String, String> p = new Map<>();
        ByteString tokenName = propertiesNameMap.get(tokenId);
        assert tokenName != null : "This token id does not exist.";

        p.put(propName, tokenName.toString());
        ByteString tokenDescription = propertiesDescriptionMap.get(tokenId);
        if (tokenDescription != null) {
            p.put(propDescription, tokenDescription.toString());
        }
        ByteString tokenImage = propertiesImageMap.get(tokenId);
        if (tokenImage != null) {
            p.put(propImage, tokenImage.toString());
        }
        ByteString tokenURI = propertiesTokenURIMap.get(tokenId);
        if (tokenURI != null) {
            p.put(propTokenURI, tokenURI.toString());
        }
        return p;
    }

    // Events

    @DisplayName("Mint")
    private static Event3Args<Hash160, ByteString, Map<String, String>> onMint;

    @DisplayName("Transfer")
    static Event4Args<Hash160, Hash160, Integer, ByteString> onTransfer;

    // Deploy, Update, Destroy

    @OnDeployment
    public static void deploy(Object data, boolean update) {
        if (!update) {
            throwIfSignerIsNotContractOwner();
            contractMap.put(totalSupplyKey, 0);
        }
    }

    public static void update(ByteString script, String manifest) {
        throwIfSignerIsNotContractOwner();
        ContractManagement.update(script, manifest);
    }

    public static void destroy() {
        throwIfSignerIsNotContractOwner();
        ContractManagement.destroy();
    }

    // Custom Methods

    @Safe
    public static Hash160 contractOwner() {
        return contractOwner;
    }

    public static void mint(Hash160 owner, ByteString tokenId, Map<String, String> properties) {
        assert Runtime.checkWitness(contractOwner) : "No authorization.";
        assert registryMap.get(tokenId) == null : "This token id already exists.";
        assert properties.containsKey(propName) : "The properties must contain a value for the key 'name'.";

        String tokenName = properties.get(propName);
        propertiesNameMap.put(tokenId, tokenName);

        if (properties.containsKey(propDescription)) {
            String description = properties.get(propDescription);
            propertiesDescriptionMap.put(tokenId, description);
        }
        if (properties.containsKey(propImage)) {
            String image = properties.get(propImage);
            propertiesImageMap.put(tokenId, image);
        }
        if (properties.containsKey(propTokenURI)) {
            String tokenURI = properties.get(propTokenURI);
            propertiesTokenURIMap.put(tokenId, tokenURI);
        }

        registryMap.put(tokenId, tokenId);
        ownerOfMap.put(tokenId, owner.toByteArray());
        new StorageMap(ctx, createTokensOfPrefix(owner)).put(tokenId, 1);

        increaseBalanceByOne(owner);
        incrementTotalSupplyByOne();
        onMint.fire(owner, tokenId, properties);
    }

    public static boolean burn(ByteString tokenId) {
        Hash160 owner = ownerOf(tokenId);
        assert owner != null : "This token id does not exist.";
        throwIfSignerIsNotOwner(owner);

        registryMap.delete(tokenId);
        propertiesNameMap.delete(tokenId);
        propertiesDescriptionMap.delete(tokenId);
        propertiesImageMap.delete(tokenId);
        propertiesTokenURIMap.delete(tokenId);
        ownerOfMap.delete(tokenId);

        new StorageMap(ctx, createTokensOfPrefix(owner)).delete(tokenId);
        decreaseBalanceByOne(owner);
        decrementTotalSupplyByOne();
        return true;
    }

    // Private Helper Methods

    private static void throwIfSignerIsNotContractOwner() {
        assert Runtime.checkWitness(contractOwner) : "No authorization.";
    }

    private static void throwIfSignerIsNotOwner(Hash160 owner) {
        assert Runtime.checkWitness(owner) : "No authorization.";
    }

    private static void increaseBalanceByOne(Hash160 owner) {
        balanceMap.put(owner.toByteArray(), balanceOf(owner) + 1);
    }

    private static void decreaseBalanceByOne(Hash160 owner) {
        balanceMap.put(owner.toByteArray(), balanceOf(owner) - 1);
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

}
