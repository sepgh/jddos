package gh.sep.attacks.jdDos.entity;

public class RequestProxy {
    private String address;
    private int port;

    public RequestProxy(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return address + ":" + port;
    }
}
