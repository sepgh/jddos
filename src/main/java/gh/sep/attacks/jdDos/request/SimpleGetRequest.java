package gh.sep.attacks.jdDos.request;

import gh.sep.attacks.jdDos.entity.RequestProxy;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class SimpleGetRequest implements Request {
    private final String address;
    private RequestListener requestListener;

    public SimpleGetRequest(String address) {
        this.address = address;
    }

    @Override
    public void make() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(getAddress());
        CloseableHttpResponse response = null;
        try {
            response = (CloseableHttpResponse) httpClient.execute(request);
            if(requestListener != null)
                requestListener.onResponse(response.getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(response != null)
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void setRequestListener(RequestListener requestListener) {
        this.requestListener = requestListener;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public RequestProxy getRequestProxy() {
        return null;
    }

    @Override
    public void addHeader(String key, String value) {

    }

    @Override
    public void setUserAgent(String userAgent) {

    }
}
