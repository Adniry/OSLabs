package lab1;

import static lab1.Main.getRandomNumberUsingNextInt;

public class Process {
    enum ProcessType {
        MAIN,
        BACK
    }
    int id;
    int estimatedTime;
    int runTime;
    int waitTime;
    long initTime;
    long startTime;

    ProcessType type;
    Process (int id, ProcessType type, int minEstimatedTime, int maxEstimatedTime) {
        this.id = id;
        estimatedTime = getRandomNumberUsingNextInt(minEstimatedTime, maxEstimatedTime);
        runTime = 0;
        waitTime = 0;
        this.type = type;
        initTime = 0;
    }

    public boolean process(long startTime){
        if(this.startTime == 0){
            this.startTime = startTime;
        }
        runTime++;
        if(estimatedTime == runTime){
            return false;
        }
        return true;
    }

    public void waitFor(){
        waitTime++;
    }

    public ProcessType getType() {
        return type;
    }

    public void setInitTime(long initTime){
        this.initTime = initTime;
    }

    public int getFullTime(){
        return runTime + waitTime;
    }

    public long getEndTime(){
        return startTime + getFullTime();
    }

    public long getInitStartDelay(){
        return startTime - initTime;
    }
}
