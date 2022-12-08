package tgs;

import net.sf.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import utils.EncryUtil;


public class TGSThread extends Thread{
    Socket socket = null;
    InetAddress inetAddress=null;
    TGSSearch Search = new TGSSearch();
    String K_c_ss= RandomStringUtils.randomAlphanumeric(32);
    String K_tgs="def";
    String K_ss="hij";

    public TGSThread(Socket socket,InetAddress inetAddress) {
        this.socket = socket;
        this.inetAddress=inetAddress;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        OutputStream outputStream = null;
        OutputStreamWriter writer = null;
        try {
            inputStream = socket.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String info = null;

            while ((info = bufferedReader.readLine()) != null) {
                //接收客户端发送的报文
                JSONObject Num3=JSONObject.fromObject(info);
                JSONObject message3=Num3.getJSONObject("message");

                String SSip=message3.get("SSip").toString();

                String str1=message3.getString("TGT");
                str1=EncryUtil.decrypt(str1,K_tgs);
                JSONObject TGT=JSONObject.fromObject(str1);
                String K_tgs_c=TGT.get("K_tgs_c").toString();
                int Clientid_TGT=Integer.parseInt(TGT.get("Clientid").toString());
                String Clientip_TGT=TGT.get("Clientip").toString();
                String TGSip=TGT.get("TGSip").toString();
                String TGTstart=TGT.get("TGTstart").toString();
                String TGTend=TGT.get("TGTend").toString();

                str1=message3.getString("CTGSauth");
                str1=EncryUtil.decrypt(str1,K_tgs_c);
                JSONObject CTGSauth=JSONObject.fromObject(str1);
                int Clientid_CTG=Integer.parseInt(CTGSauth.get("Clientid").toString());
                String Clientip_CTG=CTGSauth.get("Clientip").toString();
                String CTtime=CTGSauth.get("CTtime").toString();


                //发送给客户端报文
                JSONObject Num4 = new JSONObject();
                Num4.put("msgType",4);
                Num4.put("sendIp",TGSip);
                Num4.put("receieveIp",Clientip_TGT);
                JSONObject message4 = new JSONObject();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                String str=df.format(new Date());
                int tag=1;
                for(int i=0;i<str.length();i++){
                    if(Character.isDigit(str.charAt(i))){
                        if(str.charAt(i)<TGTend.charAt(i)) break;
                        else if(str.charAt(i)>TGTend.charAt(i)){
                            tag=0;
                            break;
                        }
                    }
                }
                String sql="select * from getip where server_ip="+"'"+SSip+"'";
                int ifError=Search.search(sql);

                if(Clientid_TGT!=Clientid_CTG||!Clientip_TGT.equals(Clientip_CTG)||tag==0||ifError==1){
                    Num4.put("ifError",1);
                    Num4.put("ifSign",0);
                    String tmp="";
                    if(Clientid_TGT!=Clientid_CTG)
                        tmp="ClientidError";
                    else if(!Clientip_TGT.equals(Clientip_CTG))
                        tmp="ClientipError";
                    else if(tag==0)
                        tmp="TGTError";
                    else if(ifError==1)
                        tmp="ServerError";
                    message4.put("msgError",tmp);
                    Num4.put("message",message4);
                    Num4.put("sign","");
                }
                else{
                    Num4.put("ifError",0);
                    Num4.put("ifSign",1);

                    JSONObject ST=new JSONObject();
                    ST.put("Clientid",Clientid_TGT);
                    ST.put("Clientip",Clientip_TGT);
                    ST.put("SSip",SSip);
                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    String str2=df1.format(new Date());
                    ST.put("STstart",str2);
                    ST.put("STend","2021-09-10 20:03:05");
                    ST.put("K_c_ss",K_c_ss);
                    str1=EncryUtil.encrypt(ST.toString(),K_ss);

                    message4.put("SSip",SSip);
                    message4.put("STstart",str2);
                    message4.put("STend","2021-09-10 20:03:05");
                    message4.put("K_c_ss",K_c_ss);
                    message4.put("ST",str1);
                    str1=EncryUtil.encrypt(message4.toString(),K_tgs_c);

                    Num4.put("message",str1);
                    Num4.put("sign",message4);
                }


                outputStream = socket.getOutputStream();
                writer = new OutputStreamWriter(outputStream, "UTF-8");

                writer.write(Num4.toString()+"\n");
                writer.flush();

            }
            socket.shutdownInput();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
