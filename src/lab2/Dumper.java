package lab2;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringJoiner;

public class Dumper {
    public static void dumpResponses(Automata.Terminal t, int[] prices, int maxInput) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter("/home/adniry/Pictures/OS/src/lab2/output" + t.id + ".txt"));

        final int nominalsAmount = Automata.idxToNominal.length;
        for(int i = 0; i < t.responses.length; i++){
            StringJoiner joiner = new StringJoiner(" + ");
            final int[] response = t.responses[i];
            final int remainder = response[nominalsAmount];
            if(remainder == 0){
                for(int j = 0; j < Automata.idxToNominal.length; j++){
                    final int coinsAmount = response[j];
                    if(coinsAmount != 0){
                        joiner.add(coinsAmount + "*" + Automata.idxToNominal[j]);
                    }
                }
                writer.write(maxInput - prices[t.requests[i]] + ": " + joiner + "\n");
            } else {
                if(remainder == -1){
                    writer.write(maxInput - prices[t.requests[i]] + ": Not enough coins in bank.\n");
                } else {
                    writer.write(maxInput - prices[t.requests[i]] + ": Cannot give " + remainder + '\n');
                }
            }
        }
        writer.close();
    }
}
