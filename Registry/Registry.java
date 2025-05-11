package Registry;


import java.util.Hashtable;
import Commons.Address;

public class Registry {
    private static Registry instance = null;
    private Hashtable<String, Entry> table;

    private Registry() {
        table = new Hashtable<String, Entry>();
    }

    public static Registry instance() {
        if(instance == null) {
            instance = new Registry();
        }
        return instance;
    }

    // entry in the registry
    public void put(String name, Entry entry) {
        table.put(name, entry);
    }

    // get address from registry
    public Address get(String name){
        return table.get(name);
    }
}
