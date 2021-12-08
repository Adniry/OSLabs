package lab1;

import java.io.IOException;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException {
        ProcessProvider provider = new ProcessProvider(0.01, 100, 80, 200);
        Logger logger = new Logger();
        Dispatcher dispatcher = new Dispatcher(provider, logger,0.8, 10, 5);
        dispatcher.evaluationLoop();
        logger.dump();


    }
}
