package gh.sep.attacks.jdDos.helper;

import gh.sep.attacks.jdDos.entity.RequestProxy;
import gh.sep.attacks.jdDos.request.Request;
import gh.sep.attacks.jdDos.request.SimpleGetRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RemoteProxyUtils {
    public static List<RequestProxy> getProxies(String address){
        List<RequestProxy> requestProxies = new ArrayList<>();
        SimpleGetRequest request = new SimpleGetRequest(address);
        request.setRequestListener(new Request.RequestListener() {
            @Override
            public void onResponse(InputStream resp) {
                try {
                    int i = -1;
                    InputStream stream = resp;
                    String line;
                    BufferedReader input = new BufferedReader(new InputStreamReader(resp));
                    while ((line = input.readLine()) != null) {
                        String[] split = line.split(":");
                        RequestProxy requestProxy = new RequestProxy(split[0], Integer.valueOf(split[1]));
                        requestProxies.add(requestProxy);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
        request.make();
        return requestProxies;
    }
}
