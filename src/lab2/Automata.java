package lab2;


import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Automata {
    int[] prices;
    int maxInput;

    AtomicInteger[] input;
    ConcurrentLinkedQueue<int[]>[] output;
    ExecutorService executor;
    static int[] idxToNominal;
    int terminalsAmount;


    public Automata(int[] prices, int maxInput, int[] idxToNominal){
        this.prices = prices;
        this.maxInput = maxInput;
        this.idxToNominal = idxToNominal;
    }

    public void start(Terminal[] terminals, int[] bank) throws InterruptedException{
        terminalsAmount = terminals.length;
        executor = Executors.newFixedThreadPool(terminalsAmount + 1);
        input = new AtomicInteger[terminalsAmount];
        for(int i = 0; i < terminalsAmount; i++){
            input[i] = new AtomicInteger(0);
        }
        output = new ConcurrentLinkedQueue[terminalsAmount];
        for(int i = 0; i < terminalsAmount; i++){
            output[i] = new ConcurrentLinkedQueue<>();
        }
        int expectedRequestsAmount = 0;
        for (Terminal t : terminals){
            expectedRequestsAmount += t.requests.length;
        }
        executor.execute(new RemainderManager(expectedRequestsAmount, bank));
        for(int i = 0; i < terminalsAmount; i++){
            executor.execute(terminals[i]);
        }
        executor.shutdown();
        while(!executor.awaitTermination(10, TimeUnit.SECONDS)){
            System.out.println("Waited 10 seconds");
        }
    }

    class Terminal extends Thread {
        int sleepTime;
        int[] requests;
        final int id;
        int[][] responses;


        public Terminal(int sleepTime, int[] requests, int id){
            this.sleepTime = sleepTime;
            this.requests = requests;
            this.id = id;
            responses = new int[requests.length][idxToNominal.length + 1];
        }

        @Override
        public void run() {
            for(int i = 0; i < requests.length; i++){
                final int price = prices[requests[i]];
                final int remainder = maxInput - price;
                final int[] response;
                try{
                    response = requestRemainder(remainder);
                } catch (InterruptedException e){
                    throw new  RuntimeException(e);
                }

                responses[i] = response;
            }

        }

        private int[] requestRemainder(int remainder) throws InterruptedException{
            input[id].set(remainder);
            int[] response;
            while(true){
                response = output[id].poll();
                if(response != null){
                    return response;
                }
                //doing other work here;
                //wait?
                Thread.sleep(sleepTime);
            }
        }
    }

    class RemainderManager extends Thread {
        final int expectedRequestsAmount;
        int requestsProcessed; // stop condition
        int[] bank;


        public RemainderManager(int expectedRequestsAmount, int[] bank) {
            this.expectedRequestsAmount = expectedRequestsAmount;
            requestsProcessed = 0;
            this.bank = bank;
        }

        @Override
        public void run(){
            while(requestsProcessed < expectedRequestsAmount){
                for(int i = 0; i < terminalsAmount; i++){
                    final int requestedRemainder = input[i].getAndSet(0);
                    if(requestedRemainder != 0){
                        final int[] response = formResponse(requestedRemainder);
                        output[i].add(response);
                        requestsProcessed++;
                    }
                }
            }
        }

        private int[] formResponse(int requestedRemainder){
            int[] response = new int[idxToNominal.length + 1];
            int remainder = requestedRemainder;
            for(int i = 0; i < idxToNominal.length; i++){
                final int nominal = idxToNominal[i];
                final int quotient = remainder / nominal;
                if(quotient > bank[i]){
                    continue;
                }
                response[i] = quotient;
                remainder = remainder % nominal;
                if (remainder == 0){
                    subtractCoins(response);
                    return response;
                }
            }

            response[idxToNominal.length] = remainder; // fail indicator
            return response;
        }

        private void subtractCoins(int[] coins){
            for (int i = 0; i < coins.length - 1; i++){
                bank[i] -= coins[i];
            }
        }
    }
}
