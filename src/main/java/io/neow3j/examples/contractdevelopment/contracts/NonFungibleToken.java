package io.neow3j.examples.contractdevelopment.contracts;

import static io.neow3j.devpack.StringLiteralHelper.addressToScriptHash;

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
import io.neow3j.devpack.annotations.Permission;
import io.neow3j.devpack.annotations.OnDeployment;
import io.neow3j.devpack.annotations.Safe;
import io.neow3j.devpack.constants.CallFlags;
import io.neow3j.devpack.constants.FindOptions;
import io.neow3j.devpack.contracts.ContractManagement;
import io.neow3j.devpack.events.Event3Args;
import io.neow3j.devpack.events.Event4Args;

@ManifestExtra(key = "author", value = "AxLabs")
// Has to call onPayment method of any receiving contract.
@Permission(contract = "*", methods = "*")
public class NonFungibleToken {

    static final Hash160 contractOwner = addressToScriptHash("NM7Aky765FG8NhhwtxjXRx7jEL1cnw7PBP");

    static final StorageContext ctx = Storage.getStorageContext();
    static final StorageMap contractMap = ctx.createMap((byte) 1);
    static final byte[] totalSupplyKey = Helper.toByteArray((byte) 1);

    static final byte[] registryPrefix = Helper.toByteArray((byte) 8);
    static final StorageMap registryMap = ctx.createMap(registryPrefix);

    static final byte[] ownerOfKey = Helper.toByteArray((byte) 10);
    static final StorageMap ownerOfMap = ctx.createMap(ownerOfKey);

    static final String propName = "name";
    static final String propDescription = "description";
    static final String propImage = "image";
    static final String propTokenURI = "tokenURI";

    static final StorageMap propertiesNameMap = ctx.createMap((byte) 12);
    static final StorageMap propertiesDescriptionMap = ctx.createMap((byte) 13);
    static final StorageMap propertiesImageMap = ctx.createMap((byte) 14);
    static final StorageMap propertiesTokenURIMap = ctx.createMap((byte) 15);

    static final byte[] balanceKey = Helper.toByteArray((byte) 20);
    static final StorageMap balanceMap = ctx.createMap(balanceKey);

    static final byte[] tokensOfKey = Helper.toByteArray((byte) 24);

    @OnDeployment
    public static void deploy(Object data, boolean update) {
        if (!update) {
            contractMap.put(totalSupplyKey, 0);
        }
    }

    @DisplayName("Transfer")
    static Event4Args<Hash160, Hash160, Integer, ByteString> onTransfer;

    @Safe
    public static Hash160 contractOwner() {
        return contractOwner;
    }

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
        return contractMap.getInteger(totalSupplyKey);
    }

    /**
     * Gets the number of tokens owned by the specified {@code owner}.
     */
    @Safe
    public static int balanceOf(Hash160 owner) {
        return getBalanceOf(owner);
    }

    /**
     * Gets the token ids owned by the specified {@code owner}.
     */
    @Safe
    public static Iterator<ByteString> tokensOf(Hash160 owner) {
        return (Iterator<ByteString>) Storage.find(
                ctx.asReadOnly(),
                createTokensOfPrefix(owner),
                FindOptions.RemovePrefix);
    }

    public static boolean transfer(Hash160 to, ByteString tokenId, Object data) throws Exception {
        Hash160 owner = ownerOf(tokenId);
        if (owner == null) {
            throw new Exception("This token id does not exist.");
        }
        if (!Runtime.checkWitness(owner)) {
            throw new Exception("No authorization.");
        }
        ownerOfMap.put(tokenId, to.toByteArray());
        ctx.createMap(createTokensOfPrefix(owner)).delete(tokenId);
        ctx.createMap(createTokensOfPrefix(to)).put(tokenId, 1);

        decrementBalanceByOne(owner);
        incrementBalanceByOne(to);

        onTransfer.fire(owner, to, 1, tokenId);

        if (ContractManagement.getContract(to) != null) {
            Contract.call(to, "onNEP11Payment", CallFlags.All,
                    new Object[]{owner, 1, tokenId, data});
        }
        return true;
    }

    private static byte[] createTokensOfPrefix(Hash160 owner) {
        return Helper.concat(tokensOfKey, owner.toByteArray());
    }

    @Safe
    public static Hash160 ownerOf(ByteString tokenId) {
        ByteString owner = ownerOfMap.get(tokenId);
        if (owner == null) {
            return null;
        }
        return new Hash160(owner);
    }

    @DisplayName("Mint")
    private static Event3Args<Hash160, ByteString, Map<String, String>> onMint;

    /**
     * Initializes a new token.
     */
    public static void mint(Hash160 owner, ByteString tokenId, Map<String, String> properties)
            throws Exception {

        if (!Runtime.checkWitness(contractOwner)) {
            throw new Exception("No authorization.");
        }
        if (registryMap.get(tokenId) != null) {
            throw new Exception("This token id already exists.");
        }

        if (!properties.containsKey(propName)) {
            throw new Exception("The properties must contain a value for the key `name`.");
        } else {
            String tokenName = properties.get(propName);
            propertiesNameMap.put(tokenId, tokenName);
        }
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
        ctx.createMap(createTokensOfPrefix(owner)).put(tokenId, 1);

        incrementBalanceByOne(owner);
        incrementTotalSupplyByOne();
        onMint.fire(owner, tokenId, properties);
    }

    @Safe
    public static Iterator<Iterator.Struct<ByteString, ByteString>> tokens() {
        return (Iterator<Iterator.Struct<ByteString, ByteString>>) Storage.find(
                ctx.asReadOnly(),
                registryPrefix,
                FindOptions.RemovePrefix);
    }

    @Safe
    public static Map<String, String> properties(ByteString tokenId) throws Exception {
        Map<String, String> p = new Map<>();
        ByteString tokenName = propertiesNameMap.get(tokenId);
        if (tokenName == null) {
            throw new Exception("This token id does not exist.");
        }

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

    private static void incrementBalanceByOne(Hash160 owner) {
        balanceMap.put(owner.toByteArray(), getBalanceOf(owner) + 1);
    }

    private static void decrementBalanceByOne(Hash160 owner) {
        balanceMap.put(owner.toByteArray(), getBalanceOf(owner) - 1);
    }

    private static int getBalanceOf(Hash160 owner) {
        if (balanceMap.get(owner.toByteArray()) == null) {
            return 0;
        }
        return balanceMap.get(owner.toByteArray()).toInt();
    }

    private static void incrementTotalSupplyByOne() {
        int updatedTotalSupply = contractMap.getInteger(totalSupplyKey) + 1;
        contractMap.put(totalSupplyKey, updatedTotalSupply);
    }

    private static void decrementTotalSupplyByOne() {
        int updatedTotalSupply = contractMap.getInteger(totalSupplyKey) - 1;
        contractMap.put(totalSupplyKey, updatedTotalSupply);
    }

    public static boolean burn(ByteString tokenId) throws Exception {
        Hash160 owner = ownerOf(tokenId);
        if (owner == null) {
            throw new Exception("This token id does not exist.");
        }
        if (!Runtime.checkWitness(owner)) {
            throw new Exception("No authorization.");
        }
        registryMap.delete(tokenId);
        propertiesNameMap.delete(tokenId);
        propertiesDescriptionMap.delete(tokenId);
        propertiesImageMap.delete(tokenId);
        propertiesTokenURIMap.delete(tokenId);
        ownerOfMap.delete(tokenId);
        ctx.createMap(createTokensOfPrefix(owner)).delete(tokenId);
        decrementBalanceByOne(owner);
        decrementTotalSupplyByOne();
        return true;
    }

    public static boolean destroy() {
        if (!Runtime.checkWitness(contractOwner)) {
            return false;
        }
        ContractManagement.destroy();
        return true;
    }

}
