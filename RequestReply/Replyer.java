
package RequestReply;

import Commons.Address;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Replyer {
    private Socket s;
    private ServerSocket srvS;
    private OutputStream oStr;
    private InputStream isStr;
    private String myName;
    private Address myAddress;

    public Replyer(String name, Address addr) throws IOException {
        this.myName = name;
        this.myAddress = addr;

        // Create server socket and throw exception if it fails
        // instead of catching and silently continuing
        this.srvS = new ServerSocket(myAddress.port(), 1000);
        System.out.println("Replyer Serversocket:" + srvS);
    }

    public void receive_transform_send(ByteStreamTransformer t){
        int length;
        byte buffer[] = null;

        try{
            s = srvS.accept();
            System.out.println("Replyer accept: Socket" + s);
            isStr = s.getInputStream();
            length = isStr.read();
            buffer = new byte[length];
            isStr.read(buffer); // read the actual data

            byte[] data = t.transform(buffer);

            // send the response
            oStr = s.getOutputStream();
            oStr.write(data.length); // first the length
            oStr.write(data);
            oStr.flush();

            oStr.close();
            isStr.close();
            s.close();
        }catch(IOException e){
            System.out.println("IOException in receive_transform_and_send_feedback: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (srvS != null && !srvS.isClosed()) {
                srvS.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing server socket: " + e.getMessage());
        }
    }
}