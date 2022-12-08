package Net_Security.Translate_Client;

import net.sf.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Connect_AS {
    //连接AS
    public JSONObject RequestAS(String connectIp,int connectPort,int clientId,int tgsId,String clientIp) {
        JSONObject js=null;
        try {
            Socket socket = new Socket(connectIp, connectPort);

            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);
            JSONObject Num1=Mkone(clientId,tgsId,clientIp,connectIp);
            pw.write(Num1.toString()+"\n");
            pw.flush();
            socket.shutdownOutput();

            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String info = null;

            while ((info = bufferedReader.readLine()) != null) {
                js = JSONObject.fromObject(info);
                if(js!=null)
                    break;
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            socket.close();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return js;
    }

    //拼接报文
    public JSONObject Mkone(int ClientId,int tgsId,String clientIp,String connectIp){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String str=df.format(new Date());

        JSONObject message1 = new JSONObject();
        //消息体设计
        message1.put("Clientid",ClientId);
        message1.put("tgsId",tgsId);
        message1.put("CAtime",str);//当时时间

        JSONObject Num1=new JSONObject();
        Num1.put("msgType",1);//消息类型
        Num1.put("ifError",0);//
        Num1.put("sendIp",clientIp);//clintIP
        Num1.put("receieveIp",connectIp);//接收者ASiP
        Num1.put("ifSign",0);//是否有数字签名
        Num1.put("message",message1);//消息体
        Num1.put("sign",message1);//数字签名

        return Num1;
    }
}