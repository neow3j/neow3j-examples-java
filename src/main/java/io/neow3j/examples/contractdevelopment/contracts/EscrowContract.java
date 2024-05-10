package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.Helper;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.Storage;
import io.neow3j.devpack.ByteString;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.OnNEP17Payment;
import io.neow3j.devpack.annotations.Permission;
import io.neow3j.devpack.annotations.Safe;
import io.neow3j.devpack.contracts.GasToken;
import io.neow3j.devpack.contracts.StdLib;
import io.neow3j.devpack.events.Event4Args;
import io.neow3j.devpack.events.Event5Args;
import io.neow3j.devpack.constants.NativeContract;

import static io.neow3j.devpack.Helper.abort;
import static io.neow3j.devpack.Storage.getReadOnlyContext;
import static io.neow3j.devpack.Storage.getStorageContext;

@DisplayName("Escrow")
@ManifestExtra(key = "author", value = "AxLabs")
@Permission(nativeContract = NativeContract.GasToken)
public class EscrowContract {

    @DisplayName("AgreementCreation")
    public static Event5Args<String, Hash160, Hash160, Hash160, Integer> onAgreementCreate;

    @DisplayName("AgreementExecution")
    public static Event4Args<String, Hash160, Hash160, Integer> onAgreementExecute;

    /**
     * The data sent with the NEP17 payment is expected to hold the values of a
     * {@link TrustAgreement}. The
     * TrustAgreement's trustor must be the sender of the funds, and the
     * TrustAgreement's amount must be the
     * amount of GAS sent.
     *
     * @param from   the sender of the funds.
     * @param amount the amount of GAS sent.
     * @param data   the data sent with the NEP17 payment, i.e., the data of a
     *               {@link TrustAgreement}.
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
    }

    /**
     * Creates the agreement with the given name.
     *
     * @param agreementName the name of the agreement to execute.
     * @param trustor       the trustor of the agreement.
     * @param beneficiary   the beneficiary of the agreement.
     * @param arbiter       the arbiter of the agreement.
     * @param amount        the amount of GAS to be held in escrow.
     */
    public static void createAgreement(String name, Hash160 trustor, Hash160 beneficiary, Hash160 arbiter, int amount) {
        TrustAgreement agreement = new TrustAgreement(name, trustor, beneficiary, arbiter, amount);

        ByteString serializedAgreement = new StdLib().serialize(agreement);
        StorageContext ctx = getStorageContext();
        if (Storage.get(ctx, name) != null) {
            abort("Agreement already exists.");
        }
        Storage.put(getStorageContext(), name, serializedAgreement);
        onAgreementCreate.fire(agreement.name, agreement.trustor, agreement.beneficiary, agreement.arbiter,
                agreement.amount);

        boolean success = new GasToken().transfer(
                trustor, Runtime.getExecutingScriptHash(), amount, agreement);

        if (!success) {
            abort("Failed to create agreement.");
        }
    }

    /**
     * Executes the agreement with the given name.
     *
     * @param agreementName the name of the agreement to execute.
     */
    public static void executeAgreement(String agreementName) {
        // Get agreement from storage.
        TrustAgreement agreement = getAgreement(agreementName);
        // Use a check-witness to see if the agreement's arbiter matches the account
        // calling the executeAgreement function.
        if (!Runtime.checkWitness(agreement.arbiter)) {
            Helper.abort("Unauthorized.");
        }
        // Delete the agreement before execution so it can't be called more than once.
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

    /**
     * Gets the agreement with the given name.
     *
     * @param agreementName the name of the agreement to get.
     * @return the agreement.
     */
    @Safe
    public static TrustAgreement getAgreement(String agreementName) {
        ByteString agreementDetails = Storage.get(getReadOnlyContext(), agreementName);
        return (TrustAgreement) new StdLib().deserialize(agreementDetails);
    }

    /**
     * Utility function to get the total NEO held by the Escrow contract.
     */
    public static int getEscrowBalance() {
        return new GasToken().balanceOf(Runtime.getExecutingScriptHash());
    }
}
