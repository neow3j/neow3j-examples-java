package io.neow3j.examples.contractinvoke;

import io.neow3j.contract.Iterator;
import io.neow3j.contract.NeoToken;
import io.neow3j.protocol.Neow3j;

import java.io.IOException;
import java.util.List;

import static io.neow3j.examples.Constants.neow3jTestnet;

public class IterateCandidates {

    // The neow3j instance used in this example.
    static final Neow3j neow3j = neow3jTestnet();

    public static void main(String[] args) throws IOException {
        NeoToken neoToken = new NeoToken(neow3j);

        Iterator<NeoToken.Candidate> iterator = neoToken.getAllCandidatesIterator();
        List<NeoToken.Candidate> candidates = iterator.traverse(50);
        iterator.terminateSession();

        System.out.println("####################\n");
        System.out.println("Candidates: " + candidates);
        System.out.println("\n####################");
    }

}
