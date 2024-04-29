package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.OnDeployment;
import io.neow3j.devpack.annotations.OnNEP17Payment;
import io.neow3j.devpack.contracts.StdLib;

import static io.neow3j.devpack.Storage.getReadOnlyContext;
import static io.neow3j.devpack.Storage.getStorageContext;
import static io.neow3j.devpack.StringLiteralHelper.addressToScriptHash;

@DisplayName("Escrow")
@ManifestExtra(key = "author", value = "Daniel Mark")
public class EscrowContract {

    static final byte[] ownerKey = new byte[] { 0x00 };

    static final String staticValue = "Hello World!";

    @OnDeployment
    public static void deploy(Object data, boolean update) {
        Storage.put(getStorageContext(), ownerKey, addressToScriptHash("NNSyinBZAr8HMhjj95MfkKD1PY7YWoDweR"));
    }

    public static Hash160 getOwner() {
        return Storage.getHash160(getReadOnlyContext(), ownerKey);
    }

    public static String getStaticValue() {
        return staticValue;
    }

    @OnNEP17Payment
    public static void transferFunds(Hash160 from, Object data) throws Exception {
        if (from == null || !Hash160.isValid(from) || from.isZero()) {
            throw new Exception("Invalid sender provided.");
        }
    }

    // Function to get address of trustor, name of transaction, beneficiary address,
    // total amount, and isRevocable flag and write it to storage in contract
    public static void createAgreement(Hash160 account, String transactionName,
            String beneficiaryAddress, Integer totalAmount, Boolean isRevocable) {
        // Store the trustor address in the contract storage
        Storage.put(getStorageContext(), account, new StdLib()
                .serialize(new Transaction(account, transactionName, beneficiaryAddress,
                        totalAmount, isRevocable)));
    }

    // Function to get the transaction details from storage in contract
    public static Transaction getAgreement(Hash160 account) {
        return (Transaction) new StdLib().deserialize(Storage.get(getReadOnlyContext(), account));
    }
}
