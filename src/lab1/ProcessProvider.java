package lab1;

import java.util.LinkedList;

import static common.RandomUtils.getRandomNumberUsingNextInt;

public class ProcessProvider {
    private double probability;

    private int provideCalls;


    private LinkedList<Process> generatedProcesses;

    ProcessProvider(double probability, int processesAmount, int minEstimatedTime, int maxEstimatedTime){
        this.probability = probability;
        generatedProcesses = new LinkedList<>();
        provideCalls = 0;
        for(int i = processesAmount; i > 0; i--){
            generatedProcesses.push(new Process(i, (Process.ProcessType.values()[(getRandomNumberUsingNextInt(0, 2))]), minEstimatedTime, maxEstimatedTime));
        }
    }

    public Process provide(long currentTime){
        provideCalls++;
        if (getRandomNumberUsingNextInt(0, 100) < probability * 100){
            Process process = generatedProcesses.pop();
            process.setInitTime(currentTime);
            return process;
        }
        return null;
    }

    boolean isEmpty(){
        return generatedProcesses.isEmpty();
    }

}
