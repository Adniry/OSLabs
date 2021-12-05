package lab1;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Logger {
    private ArrayList<Process> finishedProcesses;
    private long sumWaitMain = 0;
    private long sumFullMain = 0;
    private long sumInitStartDelayMain = 0;
    private long sumWaitBack = 0;
    private long sumFullBack = 0;
    private long sumInitStartDelayBack = 0;
    private long maxWaitMain = 0;
    private long minWaitMain = 1000000000;
    private long maxWaitBack = 0;
    private long minWaitBack = 1000000000;
    private int minEstimatedMain = 100000000;
    private int maxEstimatedMain = 0;
    private int minEstimatedBack = 100000000;
    private int maxEstimatedBack = 0;

    Logger(){
        finishedProcesses = new ArrayList<>();
    }

    void dump() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter("/home/adniry/Pictures/OS/src/lab1/output.txt"));
        writer.write("PID \t| Type | Got at | Estimated | Start | End\t| Waited | Full |\n");

        for(Process p : finishedProcesses){
            final int fullTime = p.getFullTime();
            final long endTime = p.getEndTime();

            StringBuilder builder = new StringBuilder();
            if(p.type == Process.ProcessType.MAIN){
                logMainValues(p);
            } else {
                logBackValues(p);
            }
            appendNum(p.id, 8, builder);
            builder.append(' ').append(p.type).append(" |");
            appendNum(p.initTime, 8, builder);
            appendNum(p.estimatedTime, 11, builder);
            appendNum(p.startTime, 7, builder);
            appendNum(endTime, 7, builder);
            appendNum(p.waitTime, 8, builder);
            appendNum(fullTime, 6, builder);
            builder.append('\n');
            writer.write(builder.toString());

        }
        writer.close();
        printMainStats();
        printBackStats();
    }

    private void logMainValues(Process p){
        if(p.waitTime > maxWaitMain){
            maxWaitMain = p.waitTime;
        }
        if(p.waitTime < minWaitMain){
            minWaitMain = p.waitTime;
        }
        if(p.estimatedTime > maxEstimatedMain){
            maxEstimatedMain = p.estimatedTime;
        }
        if(p.estimatedTime < minEstimatedMain){
            minEstimatedMain = p.estimatedTime;
        }
        sumFullMain += p.getFullTime();
        sumWaitMain += p.waitTime;
        sumInitStartDelayMain += p.getInitStartDelay();
    }

    private void logBackValues(Process p){
        if(p.waitTime > maxWaitBack){
            maxWaitBack = p.waitTime;
        }
        if(p.waitTime < minWaitBack){
            minWaitBack = p.waitTime;
        }
        if(p.estimatedTime > maxEstimatedBack){
            maxEstimatedBack = p.estimatedTime;
        }
        if(p.estimatedTime < minEstimatedBack){
            minEstimatedBack = p.estimatedTime;
        }
        sumFullBack += p.getFullTime();
        sumWaitBack += p.waitTime;
        sumInitStartDelayBack += p.getInitStartDelay();
    }

    private void printMainStats(){
        System.out.println();
        System.out.println("Main stats:");
        printStats(sumFullMain, sumWaitMain, sumInitStartDelayMain, maxWaitMain, minWaitMain, maxEstimatedMain, minEstimatedMain);
    }
    
    private void printBackStats(){
        System.out.println();
        System.out.println("Back stats:");
        printStats(sumFullBack, sumWaitBack, sumInitStartDelayBack, maxWaitBack, minWaitBack, maxEstimatedBack, minEstimatedBack);
    }

    private void printStats(long sumFull, long sumWait, long sumDelay, long maxWait, long minWait, int maxEst, int minEst){
        final int len = finishedProcesses.size();
        final double avgFull = sumFull/len;
        final double avgWait = sumWait/len;
        final double waitPercentage = avgWait/avgFull * 100;
        final double avgInitStartDelay = sumDelay/len;
        System.out.println("Min estimated: " + minEst);
        System.out.println("Max estimated: " + maxEst);
        System.out.println("Full avg: " + avgFull);
        System.out.println("Wait avg: " + avgWait);
        System.out.println("Wait percentage: " + waitPercentage + '%');
        System.out.println("Init-start delay Back: " + avgInitStartDelay);
        System.out.println("Max wait: " + maxWait);
        System.out.println("Min wait: " + minWait);
    }

    public void add(Process p){
        finishedProcesses.add(p);
    }

    private void appendNum (long num, int len, StringBuilder builder){
        int mul = 1;
        int length = 0;
        while(mul <= num){
            mul *= 10;
            length ++;
        }
        if(length == 0){
            length++;
        }
        builder.append(num);
        for (int i = 0; i < len - length; i++){
            builder.append(' ');    
        }
        builder.append('|');
    }
}
