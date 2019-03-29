package gh.sep.attacks.jdDos.request;

import gh.sep.attacks.jdDos.entity.RequestProxy;
import gh.sep.attacks.jdDos.entity.RequestProxyRepository;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRequest implements Request {
    protected String address;
    protected List<Header> headers = new ArrayList<>();
    private volatile RequestProxy proxy;
    private volatile RequestListener requestListener;
    private String userAgent;

    public AbstractRequest(String address, RequestProxyRepository requestProxyRepository) {
        this.address = address;
        this.proxy = requestProxyRepository.getRequestProxy();
    }

    public AbstractRequest(String address, RequestProxyRepository requestProxyRepository, List<Header> headers) {
        this(address, requestProxyRepository);
        this.headers = headers;
    }

    public void make(){
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", new MyConnectionSocketFactory())
                .register("https", new MySSLConnectionSocketFactory(SSLContexts.createSystemDefault())).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg, new FakeDnsResolver());
        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();

        InetSocketAddress socksaddr = new InetSocketAddress(proxy.getAddress(), proxy.getPort());
        HttpClientContext context = HttpClientContext.create();
        context.setAttribute("socks.address", socksaddr);

        makeRequest(httpclient, context);
    }

    public void addHeader(String key, String value){
        headers.add(new BasicHeader(key, value));
    }

    protected abstract void makeRequest(HttpClient httpClient, HttpClientContext context);

    static class FakeDnsResolver implements DnsResolver {
        public InetAddress[] resolve(String host) throws UnknownHostException {
            // Return some fake DNS record for every request, we won't be using it
            return new InetAddress[] { InetAddress.getByAddress(new byte[] { 1, 1, 1, 1 }) };
        }
    }

    static class MyConnectionSocketFactory extends PlainConnectionSocketFactory {
        @Override
        public Socket createSocket(final HttpContext context) throws IOException {
            InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
            return new Socket(proxy);
        }

        @Override
        public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress,
                                    InetSocketAddress localAddress, HttpContext context) throws IOException {
            // Convert address to unresolved
            InetSocketAddress unresolvedRemote = InetSocketAddress
                    .createUnresolved(host.getHostName(), remoteAddress.getPort());
            return super.connectSocket(connectTimeout, socket, host, unresolvedRemote, localAddress, context);
        }
    }

    static class MySSLConnectionSocketFactory extends SSLConnectionSocketFactory {

        public MySSLConnectionSocketFactory(final SSLContext sslContext) {
            // You may need this verifier if target site's certificate is not secure
            super(sslContext, ALLOW_ALL_HOSTNAME_VERIFIER);
        }

        @Override
        public Socket createSocket(final HttpContext context) throws IOException {
            InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
            return new Socket(proxy);
        }

        @Override
        public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress,
                                    InetSocketAddress localAddress, HttpContext context) throws IOException {
            // Convert address to unresolved
            InetSocketAddress unresolvedRemote = InetSocketAddress
                    .createUnresolved(host.getHostName(), remoteAddress.getPort());
            return super.connectSocket(connectTimeout, socket, host, unresolvedRemote, localAddress, context);
        }
    }

    protected void handleError(Throwable t){
        if(requestListener != null)
            requestListener.onError(t);
    }

    protected void handleRe1sponse(InputStream inputStream){
        if(requestListener != null)
            requestListener.onResponse(inputStream);
    }

    @Override
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public RequestProxy getRequestProxy() {
        return proxy;
    }

    public String getAddress() {
        return address;
    }

    protected List<Header> getHeaders() {
        return headers;
    }

    public void setRequestListener(RequestListener requestListener) {
        this.requestListener = requestListener;
    }

    protected void fixHeaders(HttpRequestBase httpRequestBase){
        for(Header header: headers){
            httpRequestBase.addHeader(header);
        }
    }

    protected void fixUserAgent(HttpRequestBase httpRequestBase){
        httpRequestBase.setHeader("User-Agent", userAgent);
    }

}
