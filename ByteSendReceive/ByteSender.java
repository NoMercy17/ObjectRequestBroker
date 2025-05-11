package ByteSendReceive;

import Commons.Address;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ByteSender {
    private Socket s;
    private String myname;
    private OutputStream oStr; // we get an output stream from our socket

    public ByteSender(String name) {
        myname = name;
    }

    public void deliver(Address destination, byte[] data) {
        try {
            s = new Socket(destination.getHost(), destination.port());
            System.out.println("Sender: Socket" + s);
            oStr = s.getOutputStream();
            oStr.write(data.length);
            oStr.write(data);
            oStr.flush(); // Flushes this output stream and forces any buffered output bytes to be written out
            oStr.close();
            s.close();
        }catch(IOException e){
            System.out.println("OPPException in deliver");
        }
    }
}
