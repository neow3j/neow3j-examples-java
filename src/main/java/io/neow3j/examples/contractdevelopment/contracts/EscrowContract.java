package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.Helper;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.OnNEP17Payment;
import io.neow3j.devpack.annotations.Safe;
import io.neow3j.devpack.contracts.GasToken;
import io.neow3j.devpack.contracts.StdLib;
import io.neow3j.devpack.events.Event4Args;
import io.neow3j.devpack.events.Event5Args;

import static io.neow3j.devpack.Helper.abort;
import static io.neow3j.devpack.Storage.getReadOnlyContext;
import static io.neow3j.devpack.Storage.getStorageContext;

@DisplayName("Escrow")
@ManifestExtra(key = "author", value = "AxLabs")
public class EscrowContract {

    @DisplayName("AgreementCreation")
    public static Event5Args<String, Hash160, Hash160, Integer, Hash160> onAgreementCreate;

    @DisplayName("AgreementExecution")
    public static Event4Args<String, Hash160, Hash160, Integer> onAgreementExecute;

    /**
     * The data sent with the NEP17 payment is expected to hold the values of a {@link TrustAgreement}. The
     * TrustAgreement's trustor must be the sender of the funds, and the TrustAgreement's amount must be the
     * amount of GAS sent.
     *
     * @param from   the sender of the funds.
     * @param amount the amount of GAS sent.
     * @param data   the data sent with the NEP17 payment, i.e., the data of a {@link TrustAgreement}.
     */
    @OnNEP17Payment
    public static void onNep17Payment(Hash160 from, int amount, Object data) {
        if (Runtime.getCallingScriptHash() != new GasToken().getHash()) {
            abort("Only GAS is accepted.");
        }
        TrustAgreement agreement = (TrustAgreement) data;
        if (!TrustAgreement.isValid(agreement)) {
            abort("Invalid agreement provided.");
        }
        if (from != agreement.trustor) {
            abort("The trustor must be the sender of the funds.");
        }
        if (amount != agreement.amount) {
            abort("The amount of GAS sent must match the agreement's amount.");
        }

        ByteString serializedAgreement = new StdLib().serialize(agreement);
        // Store the agreement using the name as the key.
        Storage.put(getStorageContext(), agreement.name, serializedAgreement);
        // Fire the AgreementCreation event.
        onAgreementCreate.fire(agreement.name, agreement.trustor, agreement.beneficiary, agreement.amount, agreement.arbitor);
    }

    /**
     * Executes the agreement with the given name.
     *
     * @param agreementName the name of the agreement to execute.
     */
    public static void executeAgreement(String agreementName) {
        // Get agreement from storage.
        TrustAgreement agreement = getAgreement(agreementName);
        // Use a check-witness of the agreement's arbitor.
        if (!Runtime.checkWitness(agreement.arbitor)) {
            Helper.abort("Unauthorized.");
        }
        // Delete the agreement before execution.
        Storage.delete(Storage.getStorageContext(), agreementName);
        // Transfer the funds based on the agreement.
        boolean success = new GasToken().transfer(Runtime.getExecutingScriptHash(), agreement.beneficiary,
                agreement.amount, null);
        if (!success) {
            abort("Failed to transfer funds.");
        }
        // Fire the AgreementExecution event.
        onAgreementExecute.fire(agreement.name, agreement.trustor, agreement.beneficiary, agreement.amount);
    }

    @Safe
    public static TrustAgreement getAgreement(String agreementName) {
        ByteString transactionDetails = Storage.get(getReadOnlyContext(), agreementName);
        return (TrustAgreement) new StdLib().deserialize(transactionDetails);
    }

}
