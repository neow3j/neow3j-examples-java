package io.neow3j.examples.contractinvoke;

import static io.neow3j.examples.Constants.NEOW3J;
import java.io.IOException;
import java.util.List;
import io.neow3j.contract.NeoToken;
import io.neow3j.crypto.ECKeyPair.ECPublicKey;
import io.neow3j.utils.Numeric;

public class FetchValidators {

    public static void main(String[] args) throws IOException {

        // Setup the NeoToken class with a node connection for further calls to the contract.
        NeoToken neo = new NeoToken(NEOW3J);

        // Get the public keys of nodes that are validators of the network.
        List<ECPublicKey> validators = neo.getNextBlockValidators();

        System.out.println("####################\n");
        System.out.printf("The Neo network has %d validators. Their public keys are: %n",
                validators.size());
        for (ECPublicKey validator : validators) {
            System.out.println(" - " + Numeric.toHexStringNoPrefix(validator.getEncoded(true)));
        }
        System.out.println("\n####################");
    }
}
