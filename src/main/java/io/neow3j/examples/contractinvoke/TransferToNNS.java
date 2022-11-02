package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.GasToken;
import io.neow3j.contract.types.NNSName;
import io.neow3j.examples.Utils;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;

import java.math.BigDecimal;
import java.math.BigInteger;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOW3J_TESTNET;

public class TransferToNNS {

    public static void main(String[] args) throws Throwable {
        GasToken gasToken = new GasToken(NEOW3J_TESTNET);

        NNSName neow3jExampleNNS = new NNSName("example.neow3j.neo");
        BigInteger amount = gasToken.toFractions(BigDecimal.valueOf(1));

        // Resolves the provided NNS domain name's text record and transfers 1 GAS to that address.
        NeoSendRawTransaction response = gasToken.transfer(ALICE, neow3jExampleNNS, amount)
                .sign()
                .send();
        Utils.trackSentTransaction(response);
    }

}
