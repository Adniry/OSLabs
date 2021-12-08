package lab2;

import common.RandomUtils;

import java.io.IOException;

public class Main {
    public static void main (String[] args) throws InterruptedException, IOException {
        final int requestsPerTerminal = 20;
        final int[] prices = new int[] {28, 37, 50, 77, 91};
        final int maxInput = 100;
        final int[] idxToNominal = new int[]{50, 25, 10, 5, 2, 1};
        int[] bank = new int[]{5, 10, 15, 20, 25, 50};
        Automata automata = new Automata(prices, maxInput, idxToNominal);
        Automata.Terminal[] terminals = new Automata.Terminal[2];
        for(int i = 0; i < 2; i++){
            terminals[i] = automata. new Terminal(4, getRandomRequests(requestsPerTerminal), i);
        }

        automata.start(terminals, bank);
        for(Automata.Terminal t : terminals){
            Dumper.dumpResponses(t, prices, maxInput);
        }
    }

    private static int[] getRandomRequests(int s){
        int[] requests = new int[s];
        for(int i = 0;  i < s; i++){
            requests[i] = RandomUtils.getRandomNumberUsingNextInt(0, 5);
        }
        return requests;
    }
}
