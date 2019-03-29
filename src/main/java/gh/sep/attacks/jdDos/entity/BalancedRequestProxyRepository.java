package gh.sep.attacks.jdDos.entity;

import java.util.List;

public class BalancedRequestProxyRepository implements RequestProxyRepository {
    private final List<RequestProxy> requestProxies;
    private volatile int index = 0;

    public BalancedRequestProxyRepository(List<RequestProxy> requestProxies) {
        this.requestProxies = requestProxies;
    }

    @Override
    public synchronized RequestProxy getRequestProxy() {
        RequestProxy requestProxy = requestProxies.get(index);
        if(index == requestProxies.size() - 1)
            index = 0;
        else
            index++;
        return requestProxy;
    }
}
