package io.neow3j.examples.contractdevelopment.contracts;

import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.annotations.Struct;

/**
 * This struct is used in the example EscrowContract.
 */
@Struct
public class TrustAgreement {
    public String name;
    public Hash160 trustor;
    public Hash160 beneficiary;
    public Integer amount;
    public Hash160 arbitor;

    public TrustAgreement(String name, Hash160 trustor, Hash160 beneficiary, int amount, Hash160 arbitor) {
        this.name = name;
        this.trustor = trustor;
        this.beneficiary = beneficiary;
        this.amount = amount;
        this.arbitor = arbitor;
    }

    public static boolean isValid(TrustAgreement agreement) {
        return agreement.name != null &&
                Hash160.isValid(agreement.trustor) &&
                Hash160.isValid(agreement.beneficiary) &&
                agreement.amount > 0 &&
                Hash160.isValid(agreement.arbitor);
    }
}
