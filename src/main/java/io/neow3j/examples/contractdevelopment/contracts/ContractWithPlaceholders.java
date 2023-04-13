package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.StorageContext;
import io.neow3j.devpack.StringLiteralHelper;
import io.neow3j.devpack.annotations.OnDeployment;
import io.neow3j.devpack.annotations.Permission;
import io.neow3j.devpack.annotations.Safe;
import io.neow3j.devpack.contracts.FungibleToken;

import java.util.Map;

import static io.neow3j.devpack.Runtime.checkWitness;
import static io.neow3j.devpack.Storage.getStorageContext;

/**
 * This contract contains placeholders. A placeholder value can be substituted in the compilation when calling
 * {@link io.neow3j.compiler.Compiler#compile(String, Map)}. Use this feature for example if you're deploying the
 * same contract multiple times with different values that should be set in the contract's byte code. The placeholder
 * has to match the syntax "${placeholder}" and cannot be embedded in a longer string value (e.g., the placeholder
 * value in "hello ${placeholder}" cannot be substituted by the compiler).
 */
@Permission(contract = "${allowedTokenContractHash}", methods = "transfer")
public class ContractWithPlaceholders {

    static final StorageContext ctx = getStorageContext();
    static final byte[] ownerHashKey = new byte[]{0x00};
    static final byte[] ownerNameKey = new byte[]{0x01};

    static final String ownerAddress = "${ownerAddress}";
    static final FungibleToken token = new FungibleToken("${allowedTokenContractHash}");

    @OnDeployment
    public static void deploy(Object data, boolean update) {
        if (!update) {
            Storage.put(ctx, ownerHashKey, StringLiteralHelper.addressToScriptHash(ownerAddress));
            Storage.put(ctx, ownerNameKey, "${ownerName}");
        }
    }

    @Safe
    public static String getOwnerName() {
        return Storage.getString(ctx, ownerNameKey);
    }

    @Safe
    public static Hash160 getOwner() {
        return Storage.getHash160(ctx, ownerHashKey);
    }

    public static void changeOwnerName(String ownerName) throws Exception {
        if (!checkWitness(getOwner())) {
            throw new Exception("No authorization!");
        }
        Storage.put(ctx, ownerNameKey, ownerName);
    }

    @Safe
    public static String getSymbolOfAllowedContract() {
        return token.symbol();
    }

    public static boolean transfer(Hash160 from, Hash160 to, int amount) {
        return token.transfer(from, to, amount, null);
    }

}
