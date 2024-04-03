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
import io.neow3j.devpack.events.Event4Args;

@DisplayName("FurryFriends")
@ManifestExtra(key = "author", value = "AxLabs")
@SupportedStandard(neoStandard = NeoStandard.NEP_11)
@Permission(nativeContract = NativeContract.ContractManagement)
@Permission(contract = "*", methods = "onNEP11Payment")
public class NonFungibleToken {

    // Alice's address
    static final Hash160 owner = StringLiteralHelper.addressToScriptHash("NM7Aky765FG8NhhwtxjXRx7jEL1cnw7PBP");

    static final byte contractMapPrefix = 0;
    static final byte[] totalSupplyKey = new byte[]{0x00};
    static final byte[] tokensOfKey = new byte[]{0x01};

    static final int registryMapPrefix = 1;
    static final int ownerOfMapPrefix = 2;
    static final int balanceMapPrefix = 3;

    static final int propNameMapPrefix = 8;
    static final int propDescriptionMapPrefix = 9;
    static final int propImageMapPrefix = 10;
    static final int propTokenURIMapPrefix = 11;

    static final String propName = "name";
    static final String propDescription = "description";
    static final String propImage = "image";
    static final String propTokenURI = "tokenURI";

    // endregion keys of key-value pairs in NFT properties
    // region deploy, update, destroy

    @OnDeployment
    public static void deploy(Object data, boolean update) {
        if (!update) {
            StorageMap contractMap = new StorageMap(Storage.getStorageContext(), contractMapPrefix);
            contractMap.put(totalSupplyKey, 0);
        }
    }

    public static void update(ByteString script, String manifest) throws Exception {
        if (!Runtime.checkWitness(contractOwner())) {
            throw new Exception("No authorization");
        }
        new ContractManagement().update(script, manifest);
    }

    public static void destroy() throws Exception {
        if (!Runtime.checkWitness(contractOwner())) {
            throw new Exception("No authorization");
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
        return new StorageMap(Storage.getReadOnlyContext(), contractMapPrefix).getInt(totalSupplyKey);
    }

    @Safe
    public static int balanceOf(Hash160 owner) throws Exception {
        if (!Hash160.isValid(owner)) {
            throw new Exception("The parameter 'owner' must be a 20-byte address.");
        }
        return getBalance(Storage.getReadOnlyContext(), owner);
    }

    @Safe
    public static Iterator<ByteString> tokensOf(Hash160 owner) throws Exception {
        if (!Hash160.isValid(owner)) {
            throw new Exception("The parameter 'owner' must be a 20-byte address.");
        }
        return (Iterator<ByteString>) Storage.find(Storage.getReadOnlyContext(), createTokensOfPrefix(owner),
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
        if (owner != to) {
            StorageContext ctx = Storage.getStorageContext();
            new StorageMap(ctx, ownerOfMapPrefix).put(tokenId, to.toByteArray());

            new StorageMap(ctx, createTokensOfPrefix(owner)).delete(tokenId);
            new StorageMap(ctx, createTokensOfPrefix(to)).put(tokenId, 1);

            decreaseBalanceByOne(ctx, owner);
            increaseBalanceByOne(ctx, to);
        }
        if (new ContractManagement().getContract(to) != null) {
            Contract.call(to, "onNEP11Payment", CallFlags.All, new Object[]{owner, 1, tokenId, data});
        }
        onTransfer.fire(owner, to, 1, tokenId);
        return true;
    }

    // endregion NEP-11 methods
    // region non-divisible NEP-11 methods

    @Safe
    public static Hash160 ownerOf(ByteString tokenId) throws Exception {
        if (tokenId.length() > 64) {
            throw new Exception("The parameter 'tokenId' must be a valid NFT ID (64 or less bytes long).");
        }
        ByteString owner = new StorageMap(Storage.getReadOnlyContext(), ownerOfMapPrefix).get(tokenId);
        if (owner == null) {
            throw new Exception("This token id does not exist.");
        }
        return new Hash160(owner);
    }

    // endregion non-divisible NEP-11 methods
    // region optional NEP-11 methods

    @Safe
    public static Iterator<Iterator.Struct<ByteString, ByteString>> tokens() {
        return (Iterator<Iterator.Struct<ByteString, ByteString>>) new StorageMap(Storage.getReadOnlyContext(),
                registryMapPrefix).find(FindOptions.RemovePrefix);
    }

    @Safe
    public static Map<String, String> properties(ByteString tokenId) throws Exception {
        if (tokenId.length() > 64) {
            throw new Exception("The parameter 'tokenId' must be a valid NFT ID (64 or less bytes long).");
        }
        Map<String, String> p = new Map<>();
        StorageContext ctx = Storage.getReadOnlyContext();
        ByteString tokenName = new StorageMap(ctx, propNameMapPrefix).get(tokenId);
        if (tokenName == null) {
            throw new Exception("This token id does not exist.");
        }

        p.put(propName, tokenName.toString());
        ByteString tokenDescription = new StorageMap(ctx, propDescriptionMapPrefix).get(tokenId);
        if (tokenDescription != null) {
            p.put(propDescription, tokenDescription.toString());
        }
        ByteString tokenImage = new StorageMap(ctx, propImageMapPrefix).get(tokenId);
        if (tokenImage != null) {
            p.put(propImage, tokenImage.toString());
        }
        ByteString tokenURI = new StorageMap(ctx, propTokenURIMapPrefix).get(tokenId);
        if (tokenURI != null) {
            p.put(propTokenURI, tokenURI.toString());
        }
        return p;
    }

    // endregion optional NEP-11 methods
    // region events

    @DisplayName("Transfer")
    private static Event4Args<Hash160, Hash160, Integer, ByteString> onTransfer;

    // endregion events
    // region custom methods

    @Safe
    public static Hash160 contractOwner() {
        return owner;
    }

    public static void mint(Hash160 to, ByteString tokenId, Map<String, String> properties) throws Exception {
        if (!Runtime.checkWitness(contractOwner())) {
            throw new Exception("No authorization");
        }
        StorageContext ctx = Storage.getStorageContext();
        StorageMap registryMap = new StorageMap(ctx, registryMapPrefix);
        if (registryMap.get(tokenId) != null) {
            throw new Exception("This token id already exists.");
        }
        if (!properties.containsKey(propName)) {
            throw new Exception("The properties must contain a value for the key 'name'.");
        }
        String tokenName = properties.get(propName);
        new StorageMap(ctx, propNameMapPrefix).put(tokenId, tokenName);
        if (properties.containsKey(propDescription)) {
            String description = properties.get(propDescription);
            new StorageMap(ctx, propDescriptionMapPrefix).put(tokenId, description);
        }
        if (properties.containsKey(propImage)) {
            String image = properties.get(propImage);
            new StorageMap(ctx, propImageMapPrefix).put(tokenId, image);
        }
        if (properties.containsKey(propTokenURI)) {
            String tokenURI = properties.get(propTokenURI);
            new StorageMap(ctx, propTokenURIMapPrefix).put(tokenId, tokenURI);
        }

        registryMap.put(tokenId, tokenId);
        new StorageMap(ctx, ownerOfMapPrefix).put(tokenId, to.toByteArray());
        new StorageMap(ctx, createTokensOfPrefix(to)).put(tokenId, 1);

        increaseBalanceByOne(ctx, to);
        incrementTotalSupplyByOne(ctx);
        if (new ContractManagement().getContract(to) != null) {
            Contract.call(to, "onNEP11Payment", CallFlags.All, new Object[]{null, 1, tokenId, null});
        }
        onTransfer.fire(null, to, 1, tokenId);
    }

    public static void burn(ByteString tokenId) throws Exception {
        Hash160 owner;
        try {
            owner = ownerOf(tokenId);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        if (!Runtime.checkWitness(owner)) {
            throw new Exception("No authorization.");
        }

        StorageContext ctx = Storage.getStorageContext();

        new StorageMap(ctx, registryMapPrefix).delete(tokenId);
        new StorageMap(ctx, propNameMapPrefix).delete(tokenId);
        new StorageMap(ctx, propDescriptionMapPrefix).delete(tokenId);
        new StorageMap(ctx, propImageMapPrefix).delete(tokenId);
        new StorageMap(ctx, propTokenURIMapPrefix).delete(tokenId);
        new StorageMap(ctx, ownerOfMapPrefix).delete(tokenId);

        new StorageMap(ctx, createTokensOfPrefix(owner)).delete(tokenId);
        decreaseBalanceByOne(ctx, owner);
        decrementTotalSupplyByOne(ctx);
        onTransfer.fire(owner, null, 1, tokenId);
    }

    // endregion custom methods
    // region private helper methods

    private static int getBalance(StorageContext ctx, Hash160 owner) {
        return new StorageMap(ctx, balanceMapPrefix).getIntOrZero(owner.toByteArray());
    }

    private static void increaseBalanceByOne(StorageContext ctx, Hash160 owner) {
        new StorageMap(ctx, balanceMapPrefix).put(owner.toByteArray(), getBalance(ctx, owner) + 1);
    }

    private static void decreaseBalanceByOne(StorageContext ctx, Hash160 owner) {
        new StorageMap(ctx, balanceMapPrefix).put(owner.toByteArray(), getBalance(ctx, owner) - 1);
    }

    private static void incrementTotalSupplyByOne(StorageContext ctx) {
        StorageMap contractMap = new StorageMap(ctx, contractMapPrefix);
        int updatedTotalSupply = contractMap.getInt(totalSupplyKey) + 1;
        contractMap.put(totalSupplyKey, updatedTotalSupply);
    }

    private static void decrementTotalSupplyByOne(StorageContext ctx) {
        StorageMap contractMap = new StorageMap(ctx, contractMapPrefix);
        int updatedTotalSupply = contractMap.getInt(totalSupplyKey) - 1;
        contractMap.put(totalSupplyKey, updatedTotalSupply);
    }

    private static byte[] createTokensOfPrefix(Hash160 owner) {
        return Helper.concat(tokensOfKey, owner.toByteArray());
    }

    // endregion private helper methods

}
