package lab1;

import java.util.ArrayList;

public class Dispatcher {
    ArrayList<Process> backgroundProcesses;
    ArrayList<Process> mainProcesses;
    ProcessProvider provider;
    double mainToBackgroundTimeRelation;
    private int mainTime;
    private int backgroundTime;
    private int quant;
    Logger logger;

    Dispatcher (ProcessProvider provider, Logger logger, double mainToBackgroundTimeRelation, int scale, int quant){
        backgroundProcesses = new ArrayList<>();
        mainProcesses = new ArrayList<>();
        this.provider = provider;
        this.mainToBackgroundTimeRelation = mainToBackgroundTimeRelation;
        mainTime = (int)Math.round(scale * quant * mainToBackgroundTimeRelation);
        backgroundTime = scale * quant - mainTime;
        this.quant = quant;
        this.logger = logger;
    }

    public void evaluationLoop(){
        long time = 0;
        while(!(provider.isEmpty() && mainProcesses.isEmpty() && backgroundProcesses.isEmpty())){
            time = evaluateMain(time);
            time = evaluateBackground(time);
        }
    }

    private void getProcess(long currentTime){
        if(provider.isEmpty()){
            return;
        }
        Process process = provider.provide(currentTime);
        if(process == null){
            return;
        }
        if(process.getType() == Process.ProcessType.MAIN){
            mainProcesses.add(process);
        } else {
            backgroundProcesses.add(process);
        }
    }

    private long evaluateMain(long time){
        long currentTime = time;
        final long endTime = time + mainTime;
        while (true){
            if(mainProcesses.isEmpty()){
                // waiting for processes to appear
                getProcess(currentTime);
                currentTime++;
                if(currentTime > endTime){
                    return currentTime;
                }
                continue;
            }
            int i = 0;
            int q = quant;
            while(i < mainProcesses.size()){  // this loop represents Round Robin
                Process process = mainProcesses.get(i);
                while(true){ // processing one process
                    if(currentTime > endTime){
                        return currentTime;
                    }
                    if(q-- > 0){
                        makeRestWait(process, mainProcesses, backgroundProcesses);
                        if(!process.process(currentTime++)){
                            // process ended
                            finishMainProcess(i);
                            i++;
                            break;
                        }
                    } else {
                        // process time expired
                        q = quant;
                        i++;
                        break;
                    }
                }
            }
        }
    }

    private long evaluateBackground(long time){
        long currentTime = time;
        final long endTime = time + backgroundTime;
        while(true){
            if(backgroundProcesses.isEmpty()){
                // waiting for processes to appear
                getProcess(currentTime);
                currentTime++;
                if(currentTime > endTime){
                    return currentTime;
                }
                continue;
            }
            Process process = backgroundProcesses.get(0);
            while(process.process(currentTime)){
                makeRestWait(process, backgroundProcesses, mainProcesses);
                getProcess(currentTime);
                currentTime++;
                if(currentTime > endTime){
                    return currentTime;
                }
            }
            finishBackgroundProcess();
        }
    }

    private void makeRestWait(Process runnning, ArrayList<Process> currentQ, ArrayList<Process> waitingQ){
        for(Process p : currentQ){
            if(p.id != runnning.id){
                p.waitFor();
            }
        }
        for(Process p : waitingQ){
            p.waitFor();
        }
    }

    private void finishMainProcess(int idx){
        logger.add(mainProcesses.get(idx));
        mainProcesses.remove(idx);
    }

    private void finishBackgroundProcess(){
        logger.add(backgroundProcesses.get(0));
        backgroundProcesses.remove(0);
    }

}
