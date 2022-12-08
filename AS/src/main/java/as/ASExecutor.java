package as;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ASExecutor {
    //AS等待客户端连接
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(10068);
            Socket socket = null;
            int count = 0;
            System.out.println("as start");

            while (true) {
                socket = serverSocket.accept();
                InetAddress inetAddress=socket.getInetAddress();
                //利用线程处理报文
                ASThread thread=new ASThread(socket,inetAddress);
                thread.start();
                count++;
                System.out.println("client number:" + count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
