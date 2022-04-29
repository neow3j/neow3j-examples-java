package io.neow3j.examples.contractdevelopment.contracts;

import static io.neow3j.devpack.StringLiteralHelper.addressToScriptHash;

import io.neow3j.devpack.Hash160;
import io.neow3j.devpack.Runtime;
import io.neow3j.devpack.annotations.DisplayName;
import io.neow3j.devpack.annotations.ManifestExtra;
import io.neow3j.devpack.annotations.OnNEP17Payment;
import io.neow3j.devpack.annotations.OnVerification;
import io.neow3j.devpack.events.Event3Args;

@ManifestExtra(key = "name", value = "OnVerificationContract")
@ManifestExtra(key = "author", value = "AxLabs")
public class OnVerificationContract {

    static final Hash160 owner = addressToScriptHash("NM7Aky765FG8NhhwtxjXRx7jEL1cnw7PBP");

    @DisplayName("onPayment")
    static Event3Args<Hash160, Integer, Object> onPayment;

    @OnVerification
    public static boolean verify(String value) throws Exception {
        return Runtime.checkWitness(owner) && value == "hello, world!";
    }

    @OnNEP17Payment
    public static void onPayment(Hash160 from, int amount, Object data) {
        onPayment.fire(from, amount, data);
    }

}
