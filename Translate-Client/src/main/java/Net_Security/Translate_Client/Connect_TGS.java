package Net_Security.Translate_Client;

import net.sf.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Connect_TGS {
    //连接TGS
    public JSONObject RequestTGS(String connectIp,int connectPort,int clientId,String serverIp,String TGT,String clientIp){
        JSONObject js=null;
        try {
            Socket socket = new Socket(connectIp, connectPort);

            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);
            JSONObject Num3=Mkthree(clientId,clientIp,connectIp,TGT,serverIp);
            pw.write(Num3.toString()+"\n");
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
    public JSONObject Mkthree(int ClientId,String clientIp,String connectIp,String TGT,String serverIp){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String str=df.format(new Date());

        JSONObject CTGSauth_M = new JSONObject();
        CTGSauth_M.put("Clientid",ClientId);
        CTGSauth_M.put("Clientip",clientIp);
        CTGSauth_M.put("CTtime",str);
        String CTGSauth=EncryUtil.encrypt(CTGSauth_M.toString(),LoginListener.K_tgs_c);

        JSONObject message3 = new JSONObject();

        message3.put("SSip",serverIp);
        message3.put("TGT",TGT);
        message3.put("CTGSauth",CTGSauth);


        JSONObject Num3=new JSONObject();
        Num3.put("msgType",3);
        Num3.put("ifError",0);
        Num3.put("sendIp",clientIp);
        Num3.put("receieveIp",connectIp);
        Num3.put("ifSign",0);
        Num3.put("message",message3);
        Num3.put("sign",message3);

        return Num3;
    }
}

