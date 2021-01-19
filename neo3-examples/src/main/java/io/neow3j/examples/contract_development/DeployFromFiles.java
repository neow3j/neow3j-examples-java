package io.neow3j.examples.contract_development;

import io.neow3j.contract.NefFile;
import io.neow3j.contract.ScriptHash;
import io.neow3j.contract.SmartContract;
import io.neow3j.contract.ManagementContract;
import io.neow3j.model.NeoConfig;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.ObjectMapperFactory;
import io.neow3j.protocol.core.methods.response.ContractManifest;
import io.neow3j.protocol.core.methods.response.NeoSendRawTransaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.transaction.Signer;
import io.neow3j.wallet.Account;
import io.neow3j.wallet.Wallet;
import java.io.File;
import java.io.FileInputStream;

// Shows how to read a smart contract's files from the disk and deployed it on through a local
// neo-node.
public class DeployFromFiles {

    public static void main(String[] args) throws Throwable {

        // Set the magic number according to the Neo network's configuration. It is used when
        // signing transactions.
        NeoConfig.setMagicNumber(new byte[]{0x01, 0x03, 0x00, 0x0}); // Magic number 769

        // Set up the connection to the neo-node
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:40332"));
        // Setup an account and wallet for signing the transaction. Make sure that the account has a
        // sufficient GAS balance to pay for the deployment.
        Account a = Account.fromWIF("L3kCZj6QbFPwbsVhxnB8nUERDy4mhCSrWJew4u5Qh5QmGMfnCTda");
        Wallet w = Wallet.withAccounts(a);

        // Retrieve the contract files.
        NefFile nefFile = NefFile.readFromFile(new File("./build/neow3j/AxLabsToken.nef"));
        ContractManifest manifest;
        try (FileInputStream s = new FileInputStream("./build/neow3j/AxLabsToken.manifest.json")) {
            manifest = ObjectMapperFactory.getObjectMapper().readValue(s, ContractManifest.class);
        }

        // Deploy the contract's NEF and manifest. This creates, signs and send a transaction to
        // the neo-node.
        NeoSendRawTransaction response = new ManagementContract(neow3j).deploy(nefFile, manifest)
                .wallet(w)
                .signers(Signer.calledByEntry(a.getScriptHash()))
                .sign()
                .send();

        if (response.hasError()) {
            System.out.printf("Deployment was not successful. Error message from neo-node "
                    + "was: '%s'\n", response.getError().getMessage());
        }
        
        ScriptHash scriptHash = SmartContract.getContractHash(a.getScriptHash(), nefFile.getScript());
        System.out.printf("Script hash of the deployd contract: %s\n", scriptHash.toString());
    }

}
