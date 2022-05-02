package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.FungibleToken;
import io.neow3j.transaction.AccountSigner;
import io.neow3j.transaction.Signer;
import io.neow3j.transaction.Transaction;
import io.neow3j.transaction.witnessrule.CalledByEntryCondition;
import io.neow3j.transaction.witnessrule.OrCondition;
import io.neow3j.transaction.witnessrule.ScriptHashCondition;
import io.neow3j.transaction.witnessrule.WitnessAction;
import io.neow3j.transaction.witnessrule.WitnessRule;
import io.neow3j.types.Hash160;

import java.math.BigInteger;

import static io.neow3j.examples.Constants.ALICE;
import static io.neow3j.examples.Constants.NEOW3J;

public class BuildTxWithWitnessRulesSigner {

    public static void main(String[] args) throws Throwable {
        Hash160 trustedContract = new Hash160("0xe7c27a246c701755574134aaa094b4fd5c79f78a");
        Hash160 notWellKnownContract = new Hash160("d34002490103d4333d91638411d1ec39e07aaafe");

        FungibleToken token = new FungibleToken(trustedContract, NEOW3J);

        WitnessRule witnessRule = new WitnessRule(
                WitnessAction.ALLOW, // Allow the witness to be used, if the following condition is true.
                // The condition is true if the scope is calledByEntry OR the contract that checks the witness is the
                // contract with script hash "0xe7c27a246c701755574134aaa094b4fd5c79f78a".
                // This is equal to a witness scope 'calledByEntry' combined with 'allowedContracts'.
                new OrCondition(
                        new CalledByEntryCondition(),
                        new ScriptHashCondition(notWellKnownContract)
                )
        );
        Signer signerWithWitnessRulesScope = AccountSigner.none(ALICE).setRules(witnessRule);

        Transaction transaction = token.transfer(
                        ALICE,
                        trustedContract,
                        new BigInteger("100"))
                .signers(signerWithWitnessRulesScope)
                .sign();
    }

}
