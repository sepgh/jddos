package gh.sep.attacks.jdDos.request;

import gh.sep.attacks.jdDos.entity.RequestProxyRepository;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.List;

public class JsonPostRequest extends AbstractRequest {
    private String data;

    public JsonPostRequest(String address, String data, RequestProxyRepository requestProxyRepository) {
        super(address, requestProxyRepository);
        setData(data);
    }

    public JsonPostRequest(String address, String data, RequestProxyRepository requestProxyRepository, List<Header> headers) {
        super(address, requestProxyRepository, headers);
        setData(data);
    }

    @Override
    protected void makeRequest(HttpClient httpClient, HttpClientContext context) {
        HttpPost request = new HttpPost(getAddress());
        fixHeaders(request);
        fixUserAgent(request);
        CloseableHttpResponse response = null;
        try {
            StringEntity params =new StringEntity(data);
            request.setEntity(params);
            response = (CloseableHttpResponse) httpClient.execute(request, context);
            handleRe1sponse(response.getEntity().getContent());
        } catch (IOException e) {
            handleError(e);
        } finally {
            if(response != null)
                try {
                    response.close();
                } catch (IOException e) {
                    handleError(e);
                }
        }
    }

    public void setData(String data) {
        this.data = data;
    }
}
