package RequestReply;

import Commons.Address;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Requestor {
    private Socket s;
    private OutputStream oStr;
    private InputStream isStr;
    private String myName;

    public Requestor(String name) {
        myName = name;
    }

    public byte[] deliver_and_wait(Address dest, byte[] data){
        byte[] buffer = null;
        int length;

        try{
            s = new Socket(dest.dest(), dest.port());
            System.out.println("Requestor: Socket " + s);
            oStr = s.getOutputStream();
            oStr.write(data.length);
            oStr.write(data);
            oStr.flush();

            // wait for response

            isStr = s.getInputStream();
            length = isStr.read();
            buffer = new byte[length];
            isStr.read(buffer);

            isStr.close();
            oStr.close();
            s.close();
        }catch(IOException e){
            System.out.println("IOException in deliver_and_wait_feedback");
        }
        return buffer;
    }
}
