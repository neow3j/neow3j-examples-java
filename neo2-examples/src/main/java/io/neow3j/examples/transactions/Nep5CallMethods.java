package io.neow3j.examples.transactions;

import io.neow3j.contract.Nep5;
import io.neow3j.contract.ScriptHash;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.exceptions.ErrorResponseException;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.wallet.Account;

import java.io.IOException;
import java.math.BigInteger;

public class Nep5CallMethods {

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

        System.out.println("Total Supply: " + nep5.totalSupply());
        System.out.println("Name: " + nep5.name());
        System.out.println("Symbol: " + nep5.symbol());
        System.out.println("Decimals: " + nep5.decimals());

        System.out.println("Balance1: " + nep5.balanceOf(account1.getScriptHash()));
        System.out.println("Balance2: " + nep5.balanceOf(account2.getScriptHash()));
    }

}
