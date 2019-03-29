package gh.sep.attacks.jdDos.request;

import gh.sep.attacks.jdDos.entity.RequestProxy;

import java.io.IOException;
import java.io.InputStream;

public interface Request {
    void make();
    void setRequestListener(RequestListener requestListener);
    String getAddress();
    RequestProxy getRequestProxy();
    void addHeader(String key, String value);
    void setUserAgent(String userAgent);

    interface RequestListener {
        void onResponse(InputStream resp);
        void onError(Throwable throwable);
    }

    class MyRequestListener implements RequestListener {
        @Override
        public void onResponse(InputStream response) {
            try {
                int i = -1;
                InputStream stream = response;
                while ((i = stream.read()) != -1) {
                    System.out.print((char) i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    class MySilentRequestListener implements RequestListener {

        @Override
        public void onResponse(InputStream resp) {

        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println(throwable.getMessage());
        }
    }
}
