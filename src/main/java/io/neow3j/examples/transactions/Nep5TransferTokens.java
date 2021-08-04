package io.neow3j.examples.transactions;

import io.neow3j.contract.Nep5;
import io.neow3j.contract.ScriptHash;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.exceptions.ErrorResponseException;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.wallet.Account;

import java.io.IOException;

public class Nep5TransferTokens {

    public static void main(String[] args) throws IOException, ErrorResponseException {
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:30333"));

        Account account1 = Account.fromWIF("KxDgvEKzgSBPPfuVfw67oPQBSjidEiqTHURKSDL1R7yGaGYAeYnr").build();
        account1.updateAssetBalances(neow3j);

        Account account2 = Account.fromWIF("L4WoxbyHdDXdJV17FbeYW4m2dxBQFv3t3GSMcGBwzG5DsM3VgPXX").build();
        account2.updateAssetBalances(neow3j);

        ScriptHash contractScripthash = new ScriptHash("0f066cfcfef9d4e76dc9a9d679b31d697b81a39f");

        Nep5 nep5 = new Nep5.Builder(neow3j)
                .fromContract(contractScripthash)
                .build();

        Boolean transferState = nep5.transfer(account1, account2.getScriptHash(), "20");
        if (transferState) {
            System.out.println("\n####################");
            System.out.println("Transfer successful.");
            System.out.println("####################\n");
        }
    }
}
