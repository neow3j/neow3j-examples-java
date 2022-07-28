package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.Iterator;
import io.neow3j.contract.NeoToken;

import java.io.IOException;
import java.util.List;

import static io.neow3j.examples.Constants.NEOW3J_TESTNET;

public class IterateCandidates {

    public static void main(String[] args) throws IOException {
        NeoToken neoToken = new NeoToken(NEOW3J_TESTNET);

        Iterator<NeoToken.Candidate> iterator = neoToken.getAllCandidatesIterator();
        List<NeoToken.Candidate> candidates = iterator.traverse(50);
        iterator.terminateSession();

        System.out.println("####################\n");
        System.out.println("Candidates: " + candidates);
        System.out.println("\n####################");
    }

}
