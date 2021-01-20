package io.neow3j.examples.contractinvoke;

import static io.neow3j.contract.ContractParameter.hash160;
import static io.neow3j.contract.ContractParameter.integer;
import java.io.File;
import io.neow3j.contract.NefFile;
import io.neow3j.contract.ScriptHash;
import io.neow3j.contract.SmartContract;
import io.neow3j.model.NeoConfig;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.transaction.Signer;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;

public class InvokeContract {

    public static void main(String[] args) throws Throwable {
        // Set the magic number according to the Neo network's configuration. 
        NeoConfig.setMagicNumber(new byte[]{0x01, 0x03, 0x00, 0x0}); // Magic number 769
        // Set up the connection to the neo-node.
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));
        // The main account available when starting a bockchain from `deault.neo-express`.
        Account account = Account.fromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda");
        Wallet wallet = Wallet.withAccounts(account);

        // If you don't know the contract's hash you can produce it by using the contract's script
        // and the script hash of the account that deployed the contract.
        NefFile nefFile = NefFile.readFromFile(new File("./build/neow3j/BongoCatToken.nef"));
        ScriptHash contractHash = SmartContract.getContractHash(account.getScriptHash(), nefFile.getScript());

        // Setup an wrapper to invoke the contract.
        SmartContract contract = new SmartContract(contractHash, neow3j);

        // Invoke by doing an RPC 'invokefunction' call.
        System.out.println("Symbol: " + contract.callFuncReturningString("symbol"));

        // Invoke by creating a raw transaction, signing, and sending it. 
        NeoSendRawTransaction response = contract.invokeFunction("transfer", 
                hash160(account.getScriptHash()),  // from
                hash160(new ScriptHash("d6c712eb53b1a130f59fd4e5864bdac27458a509")), // to
                integer(10)) // amount
            .signers(Signer.calledByEntry(account.getScriptHash()))
            .wallet(wallet)
            .sign()
            .send();

       }
}
