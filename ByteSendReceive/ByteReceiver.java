package ByteSendReceive;

import Commons.Address;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ByteReceiver {
    private Socket s;
    private InputStream iStr;
    private ServerSocket srvS;
    private String nyName;
    private Address myAddr;

    public ByteReceiver(String nyname, Address myaddr) {
        this.nyName = nyname;
        this.myAddr = myaddr;

        try{
            srvS = new ServerSocket(myAddr.port(), 1000); // the backing = queue length
            System.out.println("Receiver Serversocket: " + srvS);
        }catch(Exception e){
            System.out.println("Error openin server socket");
        }
    }

    public byte[] receive(){
        int length;
        byte buffer[] = null;

        try{
            s =srvS.accept(); // listen for a connection to this socket and accepts
            System.out.println("Receiver accepted: Socket"+s);
            iStr = s.getInputStream();
            length = iStr.read();
            buffer = new byte[length];
            iStr.read(buffer);
            iStr.close();
            s.close();
        }catch(IOException e){
            System.out.println("IOException in receive");
        }
        return buffer;
    }

//    protected void finalize throws Throwable{
//        super.finalize();
//        srvS.close();
//    }
}
