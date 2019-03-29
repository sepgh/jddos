package gh.sep.attacks.jdDos.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfiniteRunnableRequest implements Runnable {
    Logger logger = LoggerFactory.getLogger(InfiniteRunnableRequest.class);

    private final Request request;
    private final int sleep;
    private volatile boolean mustContinue = true;

    public InfiniteRunnableRequest(Request request, int sleep) {
        this.request = request;
        this.sleep = sleep;
    }

    public void run() {
        while (mustContinue){
            logger.debug("Making request to "+ request.getAddress());
            request.make();
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                stop();
            }
        }
    }

    public void stop(){
        mustContinue = false;
    }
}
