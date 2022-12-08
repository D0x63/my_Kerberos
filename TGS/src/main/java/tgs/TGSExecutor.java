package tgs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

//TGS等待客户端连接
public class TGSExecutor {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(10067);
            Socket socket = null;
            int count = 0;
            System.out.println("tgs start");

            while (true) {
                socket = serverSocket.accept();
                InetAddress inetAddress=socket.getInetAddress();
                //利用线程处理报文
                TGSThread thread=new TGSThread(socket,inetAddress);
                thread.start();
                count++;
                System.out.println("client number:" + count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
