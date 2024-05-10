package io.neow3j.examples.contractinvoke;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import io.neow3j.contract.GasToken;
import io.neow3j.contract.SmartContract;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.InvocationResult;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;
import io.neow3j.protocol.core.stackitem.StackItem;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.transaction.AccountSigner;
import io.neow3j.types.ContractParameter;
import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;

public class EscrowExampleCreateTrustAgreement {
        public static void main(String[] args) throws Throwable {
                /*
                 * This example will create a Trust agreement between a trustor, an arbiter and
                 * a beneficiary.
                 * 
                 * Initially, you will run the `createAgreement` function to create a Trust
                 * agreement where you will specify who the arbiter and beneficiary is. You
                 * will automatically be assigned as the trustor.
                 * 
                 * Then you will run the `getAgreement` function to get the details of the
                 * agreement you just created so you can see the agreement.
                 * 
                 * Next you will run the `executeAgreement` function provided in the
                 * `EscrowExampleExecuteTrustAgreemnt` class file to execute the agreement as
                 * the arbiter. This will transfer the funds held in the Escrow contract
                 * to the beneficiary.
                 * 
                 * In between the last 2 steps, you can run the `getContractBalance` utility
                 * function to get the total GAS balance of the Escrow to check if your code
                 * worked and the Escrow contract actually holds the balance you specified
                 * in the agreement.
                 */

                // First we will do some intial setup work for the Trust contract
                // 1. Instantiate a connection to the locally running Neo node
                Neow3j neow3j = Neow3j.build(new HttpService("http://localhost:50012"));

                // 2. Create a script hash of your locally deployed Escrow contract
                Hash160 scriptHash = new Hash160("0a47419ae8f3c19fea7f8f6100ee6813cab3b54e");

                // 3. Initialize the GAS token running on your local Neo node
                GasToken gas = new GasToken(neow3j);

                // 4. Initialize the trustor account
                Account trustorAccount = Account.fromWIF("L2g8aqX5WZS3n2ZqEtwHnYyBTY5K9RzvVsFc9zFNvSCxKFsMDx86");

                // Function calls
                createAgreement(trustorAccount, scriptHash, neow3j);
                getAgreement(scriptHash, neow3j);

                // Utility function to get the total GAS balance of the Escrow contract
                getContractBalance(scriptHash, gas, neow3j);
        }

        private static void createAgreement(Account account, Hash160 scriptHash, Neow3j neow3j) throws Throwable {
                // 1. Instantiate a scanner to get user input
                Scanner scanner = new Scanner(System.in);

                // 2. Now we will get all the information we'd need to create the agreement
                // on-chain
                // 2.1. Get the name of the agreement from user input
                System.out.println("Enter the name of the agreement:");
                String agreementName = scanner.nextLine();

                // 2.2. Get the beneficiary address from user input
                System.out.println("Enter the beneficiary address:");
                String beneficiaryAddress = scanner.nextLine();
                // Since the beneficiary name is a string by default, we need to convert it to a
                // Hash160 object that can be understood by the NeoVM
                Hash160 beneficiaryScriptHash = Hash160.fromAddress(beneficiaryAddress);

                // 2.3. Get the arbiter address from user input
                System.out.println("Enter the arbiter address:");
                String arbiterAddress = scanner.next();
                // Since the arbiter name is a string by default, we need to convert it to a
                // Hash160 object that can be understood by the NeoVM
                Hash160 arbiterScriptHash = Hash160.fromAddress(arbiterAddress);

                // 2.4. Get the total amount to be held in escrow from user input
                System.out.println("Enter the total amount to be held in escrow:");
                BigInteger totalAmount = scanner.nextBigInteger();

                // 3. Close the scanner
                scanner.close();

                // 4. Construct the parameters to be passed to the NeoVM using ContractParameter
                // class
                ContractParameter agreementNameParameter = ContractParameter.string(agreementName);
                ContractParameter trustorParameter = ContractParameter.hash160(account.getScriptHash());
                ContractParameter beneficiaryAddressParameter = ContractParameter.hash160(beneficiaryScriptHash);
                ContractParameter arbiterParameter = ContractParameter.hash160(arbiterScriptHash);
                ContractParameter totalAmountParameter = ContractParameter.integer(totalAmount);

                // 5. Call the createAgreement function in the Escrow contract
                NeoSendRawTransaction createAgreementResult = new SmartContract(scriptHash,
                                neow3j)
                                .invokeFunction("createAgreement",
                                                agreementNameParameter,
                                                trustorParameter,
                                                beneficiaryAddressParameter,
                                                arbiterParameter,
                                                totalAmountParameter)
                                .signers(AccountSigner.none(account).setAllowedContracts(GasToken.SCRIPT_HASH))
                                .sign()
                                .send();

                // 6. Print the transaction hash of the createAgreement function
                System.out.println("createAgreementResult Txn Hash --> " +
                                createAgreementResult.getSendRawTransaction().getHash());
        }

        private static void getAgreement(Hash160 scriptHash, Neow3j neow3j) throws Throwable {
                // 1. Instantiate a scanner to get user input
                Scanner scanner = new Scanner(System.in);

                // 2. Get the name of the agreement from user input
                System.out.println("Enter the name of the agreement you want to retrieve:");
                String agreementNameToGet = scanner.nextLine();

                // 3. Close the scanner
                scanner.close();

                // 4. Construct the agreement name parameter to be passed to the NeoVM using
                // ContractParameter class
                ContractParameter agreementNameParameter = ContractParameter.string(agreementNameToGet);

                // 5. Read the agreement details from Escrow contract storage
                InvocationResult getcreateAgreementResult = new SmartContract(scriptHash, neow3j)
                                .callInvokeFunction("getAgreement", Arrays.asList(agreementNameParameter))
                                .getInvocationResult();

                // 6. The result of the getAgreement function is an item on the NeoVM stack. We
                // will take the 1st item from the stack and assign it to a variable
                StackItem result = getcreateAgreementResult.getFirstStackItem();

                // 7. The result from step 6 is a list of StackItems. We will get the details of
                // the agreement from the list and store it in a List<StackItem> object
                List<StackItem> resutsAsStackItem = result.getList();

                // 8. Now we can access the agreement details by their index in the list
                String agreementName = resutsAsStackItem.get(0).getString();
                System.out.print("Agreement Name: " + agreementName + "\n");

                String trustor = resutsAsStackItem.get(1).getAddress();
                System.out.print("Trustor: " + trustor + "\n");

                String beneficiary = resutsAsStackItem.get(2).getAddress();
                System.out.print("Beneficiary: " + beneficiary + "\n");

                String arbiter = resutsAsStackItem.get(3).getAddress();
                System.out.print("Arbiter: " + arbiter + "\n");

                BigInteger totalAmount = resutsAsStackItem.get(4).getInteger();
                System.out.print("Total Amount: " + totalAmount + "\n");
        }

        // Utulity function to get contract balance
        private static void getContractBalance(Hash160 scriptHash, GasToken gas, Neow3j neow3j) throws Throwable {
                BigInteger contractBalance = gas.getBalanceOf(scriptHash);
                System.out.println("Contract Balance: " + contractBalance);
        }
}
