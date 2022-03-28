package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.ByteString;
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
import io.neow3j.devpack.constants.FindOptions;
import io.neow3j.devpack.constants.NativeContract;
import io.neow3j.devpack.constants.NeoStandard;
import io.neow3j.devpack.contracts.ContractManagement;
import io.neow3j.devpack.events.Event3Args;
import io.neow3j.devpack.events.Event4Args;

import static io.neow3j.devpack.StringLiteralHelper.addressToScriptHash;

@ManifestExtra(key = "author", value = "AxLabs")
@SupportedStandard(neoStandard = NeoStandard.NEP_11)
@Permission(nativeContract = NativeContract.ContractManagement)
public class DivisibleNonFungibleToken {

    static final Hash160 contractOwner = addressToScriptHash("NM7Aky765FG8NhhwtxjXRx7jEL1cnw7PBP");

    static final StorageContext ctx = Storage.getStorageContext();
    static final StorageMap contractMap = new StorageMap(ctx, 1);
    static final String totalSupplyKey = "totSupply";

    static final StorageMap registryMap = new StorageMap(ctx, 8);

    static final String propName = "name";
    static final String propDescription = "description";
    static final String propImage = "image";
    static final String propTokenURI = "tokenURI";
    static final StorageMap propertiesNameMap = new StorageMap(ctx, 12);
    static final StorageMap propertiesDescriptionMap = new StorageMap(ctx, 13);
    static final StorageMap propertiesImageMap = new StorageMap(ctx, 14);
    static final StorageMap propertiesTokenURIMap = new StorageMap(ctx, 15);
    static final StorageMap amountOfOwnedTokensMap = new StorageMap(ctx, 21);
    static final StorageMap totalBalanceMap = new StorageMap(ctx, 22);

    static final ByteString balanceTokenOwnerKey = new ByteString("balanceTokenOwner");
    static final ByteString ownerOfKey = new ByteString("ownerOf");
    static final ByteString tokensOfKey = new ByteString("tokensOf");

    private static final int TOKEN_DECIMALS = 8;
    private static final int FACTOR = 100_000_000;

    @DisplayName("mint")
    private static Event3Args<Hash160, ByteString, Map<String, String>> onMint;

    @DisplayName("transfer")
    private static Event4Args<Hash160, Hash160, Integer, ByteString> onTransfer;

    @OnDeployment
    public static void deploy(Object data, boolean update) {
        if (!update) {
            contractMap.put(totalSupplyKey, 0);
        }
    }

    @Safe
    public static String symbol() {
        return "NEOW";
    }

    @Safe
    public static int decimals() {
        return TOKEN_DECIMALS;
    }

    @Safe
    public static Hash160 getContractOwner() {
        return contractOwner;
    }

    @Safe
    public static int totalSupply() {
        return contractMap.getInt(totalSupplyKey);
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

    @Safe
    public static int balanceOf(Hash160 owner) {
        ByteString b = amountOfOwnedTokensMap.get(owner.toByteArray());
        if (b == null) {
            return 0;
        }
        return b.toIntOrZero();
    }

    @Safe
    public static int balanceOf(Hash160 owner, ByteString tokenId) {
        ByteString balance = new StorageMap(ctx, createTokensOfPrefix(owner)).get(tokenId);
        if (balance == null) {
            return 0;
        }
        return balance.toIntOrZero();
    }

    @Safe
    public static Iterator<ByteString> tokensOf(Hash160 owner) {
        return (Iterator<ByteString>) Storage.find(ctx.asReadOnly(), createTokensOfPrefix(owner),
                FindOptions.ValuesOnly);
    }

    @Safe
    public static Iterator<ByteString> ownerOf(ByteString tokenId) {
        return (Iterator<ByteString>) Storage.find(ctx.asReadOnly(), createOwnerOfPrefix(tokenId),
                FindOptions.ValuesOnly);
    }

    public static boolean mint(Hash160 owner, ByteString tokenId, Map<String, String> properties)
            throws Exception {
        if (!Runtime.checkWitness(contractOwner)) {
            throw new Exception("No authorization.");
        }
        if (registryMap.get(tokenId) != null) {
            throw new Exception("This token id already exists.");
        }

        String tokenName = properties.get(propName);
        if (tokenName == null) {
            throw new Exception("The properties must contain a value for the key `name`.");
        }
        propertiesNameMap.put(tokenId, tokenName);
        String description = properties.get(propDescription);
        if (description != null) {
            propertiesDescriptionMap.put(tokenId, description);
        }
        String image = properties.get(propImage);
        if (image != null) {
            propertiesImageMap.put(tokenId, image);
        }
        String tokenURI = properties.get(propTokenURI);
        if (tokenURI != null) {
            propertiesTokenURIMap.put(tokenId, tokenURI);
        }

        registryMap.put(tokenId, tokenId);
        increaseTotalSupply();
        addTokenOwner(tokenId, owner);
        addToOwnersTokens(owner, tokenId);

        increaseTotalOwnerBalance(owner, FACTOR);
        increaseBalance(owner, tokenId, FACTOR);
        int amountOfOwnedTokens = amountOfOwnedTokensMap.getInt(owner.toByteArray());
        amountOfOwnedTokensMap.put(owner.toByteArray(), amountOfOwnedTokens + 1);
        onMint.fire(owner, tokenId, properties);
        return true;
    }

    private static void addTokenOwner(ByteString tokenId, Hash160 owner) {
        new StorageMap(ctx, createOwnerOfPrefix(tokenId))
                .put(owner.toByteArray(), owner.toByteArray());
    }

    private static void removeTokenOwner(ByteString tokenId, Hash160 owner) {
        new StorageMap(ctx, createOwnerOfPrefix(tokenId)).delete(owner.toByteArray());
    }

    private static void addToOwnersTokens(Hash160 owner, ByteString tokenId) {
        new StorageMap(ctx, createTokensOfPrefix(owner)).put(tokenId, tokenId);
    }

    private static void removeOwnersToken(Hash160 owner, ByteString tokenId) {
        new StorageMap(ctx, createTokensOfPrefix(owner)).put(tokenId, tokenId);
    }

    @Safe
    public static Iterator<ByteString> tokens() {
        return (Iterator<ByteString>) registryMap.find(FindOptions.ValuesOnly);
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

    private static void increaseTotalSupply() {
        int totalSupply = totalSupply();
        contractMap.put(totalSupplyKey, totalSupply + FACTOR);
    }

    private static void increaseTotalOwnerBalance(Hash160 owner, int addition) {
        ByteString totalBalance = totalBalanceMap.get(owner.toByteArray());
        if (totalBalance == null) {
            totalBalanceMap.put(owner.toByteArray(), addition);
        } else {
            totalBalanceMap.put(owner.toByteArray(), totalBalance.toInt() + addition);
        }
    }

    private static void decreaseTotalBalance(Hash160 owner, int subtraction) throws Exception {
        ByteString totalBalance = totalBalanceMap.get(owner.toByteArray());
        if (totalBalance == null) {
            throw new Exception("Can not decrease an nonexistent balance.");
        }
        if (totalBalance.toInt() < subtraction) {
            throw new Exception("Can not subtract more than the actual balance.");
        }
        totalBalanceMap.put(owner.toByteArray(), totalBalance.toInt() - subtraction);
    }

    public static boolean transfer(Hash160 from, Hash160 to, int amount, ByteString tokenId,
            Object data) throws Exception {
        if (!Runtime.checkWitness(from)) {
            throw new Exception("No authorization.");
        }
        int balance = balanceOf(from, tokenId);
        if (amount < 0 || amount > balance) {
            throw new Exception("Amount is out of scope for your balance.");
        }

        if (amount == balance) {
            int amountOfOwnedTokens = amountOfOwnedTokensMap.getInt(from.toByteArray());
            amountOfOwnedTokensMap.put(from.toByteArray(), amountOfOwnedTokens - 1);
            removeTokenOwner(tokenId, from);
            removeOwnersToken(from, tokenId);
        }
        decreaseTotalBalance(from, amount);
        decreaseBalance(from, tokenId, amount);
        increaseTotalOwnerBalance(to, amount);
        increaseBalance(to, tokenId, amount);
        addTokenOwner(tokenId, to);
        addToOwnersTokens(to, tokenId);
        onTransfer.fire(from, to, amount, tokenId);
        return true;
    }

    private static void increaseBalance(Hash160 owner, ByteString tokenId, int addition) {
        new StorageMap(ctx, createBalancePrefix(owner))
                .put(tokenId, getBalanceOf(owner, tokenId) + addition);
    }

    private static void decreaseBalance(Hash160 owner, ByteString tokenId, int subtraction) {
        new StorageMap(ctx, createBalancePrefix(owner))
                .put(tokenId, getBalanceOf(owner, tokenId) - subtraction);
    }

    private static int getBalanceOf(Hash160 owner, ByteString tokenId) {
        ByteString balance = new StorageMap(ctx, createBalancePrefix(owner)).get(tokenId);
        if (balance == null) {
            return 0;
        }
        return balance.toInt();
    }

    public static boolean migrate(ByteString script, String manifest) throws Exception {
        if (!Runtime.checkWitness(contractOwner)) {
            throw new Exception("No authorization.");
        }
        if (script.length() == 0 || manifest.length() == 0) {
            return false;
        }
        ContractManagement.update(script, manifest);
        return true;
    }

    public static boolean destroy() throws Exception {
        if (!Runtime.checkWitness(contractOwner)) {
            throw new Exception("No authorization.");
        }
        ContractManagement.destroy();
        return true;
    }

}
