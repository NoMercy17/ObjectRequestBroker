import Registry.*;

public class Config {
    public Config() {
        Entry infoServerEntry = new Entry("localhost",8090);
        Registry.instance().put("InfoServer", infoServerEntry);

        Entry infoClientEntry = new Entry("localhost",8090);
        Registry.instance().put("InfoClient", infoClientEntry);
    }
}
