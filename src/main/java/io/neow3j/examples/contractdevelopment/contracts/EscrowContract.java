package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.OnNEP17Payment;
import io.neow3j.devpack.contracts.StdLib;

import static io.neow3j.devpack.Storage.getReadOnlyContext;
import static io.neow3j.devpack.Storage.getStorageContext;

@DisplayName("Escrow")
@ManifestExtra(key = "author", value = "Daniel Mark")
public class EscrowContract {
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
        Transaction transactionDetails = new Transaction(account, transactionName, beneficiaryAddress,
                totalAmount, isRevocable);
        // Store the trustor address in the contract storage
        Storage.put(getStorageContext(), account, new StdLib()
                .serialize(transactionDetails));
    }

    // Function to get the transaction details from storage in contract
    public static Transaction getAgreement(Hash160 account) {
        ByteString transactionDetails = Storage.get(getReadOnlyContext(), account);
        return (Transaction) new StdLib().deserialize(transactionDetails);
    }
}
