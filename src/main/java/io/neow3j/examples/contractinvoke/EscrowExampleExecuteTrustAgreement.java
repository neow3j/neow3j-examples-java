package io.neow3j.examples.contractinvoke;

import java.util.Scanner;

import io.neow3j.contract.SmartContract;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.transaction.AccountSigner;
import io.neow3j.types.ContractParameter;
import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;

public class EscrowExampleExecuteTrustAgreement {
    public static void main(String[] args) throws Throwable {
        // 1. Instantiate a connection to the locally running Neo node
        Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:50012"));
        // If you are using the neo3-privatenet-docker, you can use the following line:
        //  Neow3j neow3j = io.neow3j.examples.Constants.NEOW3J_PRIVATENET;

        // 2. Create a script hash of your locally deployed Escrow contract
        // Make sure this hash is identical to the contract hash of your deployed Escrow
        // contract.
        Hash160 scriptHash = new Hash160("0a47419ae8f3c19fea7f8f6100ee6813cab3b54e");

        // 3. Initialize the arbiter account
        // Make sure it holds some GAS. Otherwise, use the TransferGas example to transfer some GAS to it.
        Account arbiterAccount = Account.fromWIF("KzrHihgvHGpF9urkSbrbRcgrxSuVhpDWkSfWvSg97pJ5YgbdHKCQ");

        // 4. Instantiate a scanner to get user input
        Scanner scanner = new Scanner(System.in);

        // 5. Get the name of the agreement to execute from user input
        System.out.println("Enter the name of the agreement you want to retrieve:");
        String agreementNameToGet = scanner.nextLine();

        // 6. Close the scanner
        scanner.close();

        // 7. We will create a ContractParameter object for the agreement name
        ContractParameter agreementNameParameter = ContractParameter.string(agreementNameToGet);

        // 8. Now we will execute the agreement
        NeoSendRawTransaction executeAgreementResult = new SmartContract(scriptHash,
                neow3j)
                .invokeFunction("executeAgreement",
                        agreementNameParameter)
                .signers(AccountSigner.calledByEntry(arbiterAccount))
                .sign()
                .send();

        // 9. And lastly we will print the transaction hash
        System.out.println("executeAgreementResult Txn Hash --> " +
                executeAgreementResult.getSendRawTransaction().getHash());
    }
}
