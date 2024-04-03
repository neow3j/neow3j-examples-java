package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.Contract;
import io.neow3j.devpack.Hash160;
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

@DisplayName("SharedFurryFriends")
@ManifestExtra(key = "author", value = "AxLabs")
@SupportedStandard(neoStandard = NeoStandard.NEP_11)
@Permission(nativeContract = NativeContract.ContractManagement)
@Permission(contract = "*", methods = "onNEP11Payment")
public class DivisibleNonFungibleToken {

    static final int contractMapPrefix = 0;
    static final byte[] totalSupplyKey = new byte[]{0x00};

    static final int registryMapPrefix = 1;

    static final int propNameMapPrefix = 8;
    static final int propDescriptionMapPrefix = 9;
    static final int propImageMapPrefix = 10;
    static final int propTokenURIMapPrefix = 11;

    static final int amountOfOwnedTokensMapPrefix = 12;
    static final int totalBalanceMapPrefix = 13;

    static final byte[] balanceTokenOwnerKeyPrefix = new byte[]{0x20};
    static final byte[] ownerOfKeyPrefix = new byte[]{0x21};
    static final byte[] tokensOfKeyPrefix = new byte[]{0x22};

    static final String propName = "name";
    static final String propDescription = "description";
    static final String propImage = "image";
    static final String propTokenURI = "tokenURI";

    // This NFT has 8 decimals, hence, 100_000_000 fractions are equivalent to a full NFT.
    private static final int FACTOR = 100_000_000;

    // region deploy, update, destroy

    @OnDeployment
    public static void deploy(Object data, boolean update) throws Exception {
        if (!Runtime.checkWitness(contractOwner())) {
            throw new Exception("No authorization");
        }
        if (!update) {
            new StorageMap(Storage.getStorageContext(), contractMapPrefix).put(totalSupplyKey, 0);
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
        return new StorageMap(Storage.getReadOnlyContext(), contractMapPrefix).getInt(totalSupplyKey);
    }

    @Safe
    public static int balanceOf(Hash160 owner) throws Exception {
        if (!Hash160.isValid(owner)) {
            throw new Exception("The parameter 'owner' must be a 20-byte address.");
        }
        return new StorageMap(Storage.getReadOnlyContext(),
                amountOfOwnedTokensMapPrefix).getIntOrZero(owner.toByteArray());
    }

    @Safe
    public static Iterator<ByteString> tokensOf(Hash160 owner) throws Exception {
        if (!Hash160.isValid(owner)) {
            throw new Exception("The parameter 'owner' must be a 20-byte address.");
        }
        return (Iterator<ByteString>) Storage.find(Storage.getReadOnlyContext(), createTokensOfPrefix(owner),
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
        if (from != to) {
            StorageContext ctx = Storage.getStorageContext();
            StorageMap amountOfOwnedTokensMap =
                    new StorageMap(ctx, amountOfOwnedTokensMapPrefix);
            int amountOfOwnedtokens = amountOfOwnedTokensMap.getInt(from.toByteArray());
            if (amountOfOwnedtokens == 1) {
                amountOfOwnedTokensMap.delete(from.toByteArray());
            } else {
                amountOfOwnedTokensMap.put(from.toByteArray(), amountOfOwnedtokens - 1);
            }
            removeTokenOwner(ctx, tokenId, from);
            removeOwnersToken(ctx, from, tokenId);
            decreaseBalance(ctx, from, tokenId, FACTOR);
            decreaseTotalBalance(ctx, from, FACTOR);

            addTokenOwner(ctx, tokenId, to);
            addOwnersTokens(ctx, to, tokenId);
            increaseBalance(ctx, to, tokenId, FACTOR);
            increaseTotalOwnerBalance(ctx, to, FACTOR);
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
        StorageContext ctx = Storage.getStorageContext();
        if (amount == balance) {
            StorageMap amountOfOwnedTokensMap =
                    new StorageMap(ctx, amountOfOwnedTokensMapPrefix);
            int amountOfOwnedTokens = amountOfOwnedTokensMap.getInt(from.toByteArray());
            if (amountOfOwnedTokens == 1) {
                amountOfOwnedTokensMap.delete(from.toByteArray());
            } else {
                amountOfOwnedTokensMap.put(from.toByteArray(), amountOfOwnedTokens - 1);
            }
            removeTokenOwner(ctx, tokenId, from);
            removeOwnersToken(ctx, from, tokenId);
        }
        decreaseBalance(ctx, from, tokenId, amount);
        decreaseTotalBalance(ctx, from, amount);

        addTokenOwner(ctx, tokenId, to);
        addOwnersTokens(ctx, to, tokenId);
        increaseBalance(ctx, to, tokenId, amount);
        increaseTotalOwnerBalance(ctx, to, amount);
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
        return (Iterator<ByteString>) Storage.find(Storage.getReadOnlyContext(), createOwnerOfPrefix(tokenId),
                FindOptions.ValuesOnly);
    }

    @Safe
    public static int balanceOf(Hash160 owner, ByteString tokenId) {
        return new StorageMap(Storage.getReadOnlyContext().asReadOnly(),
                createTokensOfPrefix(owner)).getIntOrZero(tokenId);
    }

    // endregion divisible NEP-11 methods
    // region optional methods

    @Safe
    public static Iterator<ByteString> tokens() {
        return (Iterator<ByteString>) new StorageMap(Storage.getReadOnlyContext(), registryMapPrefix)
                .find(FindOptions.ValuesOnly);
    }

    @Safe
    public static Map<String, String> properties(ByteString tokenId) throws Exception {
        Map<String, String> p = new Map<>();
        StorageContext ctx = Storage.getStorageContext();
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

    // endregion optional methods
    // region events

    @DisplayName("Transfer")
    private static Event4Args<Hash160, Hash160, Integer, ByteString> onTransfer;

    // endregion events
    // region custom methods

    @Safe
    public static Hash160 contractOwner() {
        return StringLiteralHelper.addressToScriptHash("NM7Aky765FG8NhhwtxjXRx7jEL1cnw7PBP");
    }

    public static boolean mint(Hash160 owner, ByteString tokenId, Map<String, String> properties) throws Exception {
        if (!Runtime.checkWitness(contractOwner())) {
            throw new Exception("No authorization");
        }
        StorageContext ctx = Storage.getStorageContext();
        if (new StorageMap(ctx, registryMapPrefix).get(tokenId) != null) {
            throw new Exception("This token id already exists.");
        }

        String tokenName = properties.get(propName);
        if (tokenName == null) {
            throw new Exception("The properties must contain a value for the key `name`.");
        }
        new StorageMap(ctx, propNameMapPrefix).put(tokenId, tokenName);
        String description = properties.get(propDescription);
        if (description != null) {
            new StorageMap(ctx, propDescriptionMapPrefix).put(tokenId, description);
        }
        String image = properties.get(propImage);
        if (image != null) {
            new StorageMap(ctx, propImageMapPrefix).put(tokenId, image);
        }
        String tokenURI = properties.get(propTokenURI);
        if (tokenURI != null) {
            new StorageMap(ctx, propTokenURIMapPrefix).put(tokenId, tokenURI);
        }

        new StorageMap(ctx, registryMapPrefix).put(tokenId, tokenId);
        increaseTotalSupply(ctx);
        addTokenOwner(ctx, tokenId, owner);
        addOwnersTokens(ctx, owner, tokenId);

        increaseTotalOwnerBalance(ctx, owner, FACTOR);
        increaseBalance(ctx, owner, tokenId, FACTOR);
        StorageMap amountOfOwnedTokensMap = new StorageMap(ctx, amountOfOwnedTokensMapPrefix);
        int amountOfOwnedTokens = amountOfOwnedTokensMap.getInt(owner.toByteArray());
        amountOfOwnedTokensMap.put(owner.toByteArray(), amountOfOwnedTokens + 1);
        onTransfer.fire(null, owner, FACTOR, tokenId);
        if (new ContractManagement().getContract(owner) != null) {
            Contract.call(owner, "onNEP11Payment", CallFlags.All, new Object[]{null, FACTOR, tokenId, null});
        }
        return true;
    }

    // endregion custom methods
    // region private helper methods

    private static ByteString createOwnerOfPrefix(ByteString tokenId) {
        return new ByteString(ownerOfKeyPrefix).concat(tokenId);
    }

    private static ByteString createTokensOfPrefix(Hash160 owner) {
        return new ByteString(tokensOfKeyPrefix).concat(owner.toByteString());
    }

    private static ByteString createBalancePrefix(Hash160 owner) {
        return new ByteString(balanceTokenOwnerKeyPrefix).concat(owner.toByteString());
    }

    private static void addTokenOwner(StorageContext ctx, ByteString tokenId, Hash160 owner) {
        new StorageMap(ctx, createOwnerOfPrefix(tokenId)).put(owner.toByteArray(), owner.toByteArray());
    }

    private static void removeTokenOwner(StorageContext ctx, ByteString tokenId, Hash160 owner) {
        new StorageMap(ctx, createOwnerOfPrefix(tokenId)).delete(owner.toByteArray());
    }

    private static void addOwnersTokens(StorageContext ctx, Hash160 owner, ByteString tokenId) {
        new StorageMap(ctx, createTokensOfPrefix(owner)).put(tokenId, tokenId);
    }

    private static void removeOwnersToken(StorageContext ctx, Hash160 owner, ByteString tokenId) {
        new StorageMap(ctx, createTokensOfPrefix(owner)).delete(tokenId);
    }

    private static void increaseTotalSupply(StorageContext ctx) {
        int totalSupply = totalSupply();
        new StorageMap(ctx, contractMapPrefix).put(totalSupplyKey, totalSupply + FACTOR);
    }

    private static void increaseTotalOwnerBalance(StorageContext ctx, Hash160 owner, int addend) {
        StorageMap totalBalanceMap = new StorageMap(ctx, totalBalanceMapPrefix);
        ByteString totalBalance = totalBalanceMap.get(owner.toByteArray());
        if (totalBalance == null) {
            totalBalanceMap.put(owner.toByteArray(), addend);
        } else {
            totalBalanceMap.put(owner.toByteArray(), totalBalance.toInt() + addend);
        }
    }

    private static void decreaseTotalBalance(StorageContext ctx, Hash160 owner, int subtrahend) {
        StorageMap totalBalanceMap = new StorageMap(ctx, totalBalanceMapPrefix);
        ByteString totalBalance = totalBalanceMap.get(owner.toByteArray());
        totalBalanceMap.put(owner.toByteArray(), totalBalance.toInt() - subtrahend);
    }

    private static void increaseBalance(StorageContext ctx, Hash160 owner, ByteString tokenId, int addend) {
        new StorageMap(ctx, createBalancePrefix(owner)).put(tokenId, getBalanceOf(ctx, owner, tokenId) + addend);
    }

    private static void decreaseBalance(StorageContext ctx, Hash160 owner, ByteString tokenId, int subtrahend) {
        new StorageMap(ctx, createBalancePrefix(owner)).put(tokenId, getBalanceOf(ctx, owner, tokenId) - subtrahend);
    }

    private static int getBalanceOf(StorageContext ctx, Hash160 owner, ByteString tokenId) {
        ByteString balance = new StorageMap(ctx, createBalancePrefix(owner)).get(tokenId);
        if (balance == null) {
            return 0;
        }
        return balance.toInt();
    }

    // endregion private helper methods

}
