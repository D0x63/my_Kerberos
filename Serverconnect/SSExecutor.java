package Serverconnect;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

//SS等待客户端连接
public class SSExecutor {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(10068);
            Socket socket = null;
            int count = 0;
            System.out.println("ss start");

            while (true) {
                socket = serverSocket.accept();
                InetAddress inetAddress=socket.getInetAddress();
                //利用线程处理报文
                SSThread thread=new SSThread(socket,inetAddress);
                thread.start();
                count++;
                System.out.println("client number:" + count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
