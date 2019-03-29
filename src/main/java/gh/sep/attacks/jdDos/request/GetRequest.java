package gh.sep.attacks.jdDos.request;

import gh.sep.attacks.jdDos.entity.RequestProxyRepository;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;

import java.io.IOException;
import java.util.List;

public class GetRequest extends AbstractRequest {
    public GetRequest(String address, RequestProxyRepository requestProxyRepository) {
        super(address, requestProxyRepository);
    }

    public GetRequest(String address, RequestProxyRepository requestProxyRepository, List<Header> headers) {
        super(address, requestProxyRepository, headers);
    }

    protected void makeRequest(HttpClient httpClient, HttpClientContext context) {
        HttpGet request = new HttpGet(getAddress());
        fixHeaders(request);
        fixUserAgent(request);
        CloseableHttpResponse response = null;
        try {
            response = (CloseableHttpResponse) httpClient.execute(request, context);
            handleRe1sponse(response.getEntity().getContent());
        } catch (IOException e) {
           handleError(e);
        }finally {
            if(response != null)
            try {
                response.close();
            } catch (IOException e) {
                handleError(e);
            }
        }
    }
}
