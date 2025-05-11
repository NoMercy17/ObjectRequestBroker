package Commons;

public class Address {
    private final String host;
    private final int port;

    public Address(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // Methods used by ByteSender
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    // Method used by Replyer (keep for backward compatibility)
    public int port() {
        return port;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Address other = (Address) obj;
        return port == other.port && host.equals(other.host);
    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        return result;
    }
}