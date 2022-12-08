package Net_Security.Translate_Client;

import net.sf.json.JSONObject;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Connect_SS {
    //连接SS
	public static String send_time;
	public static Nonduicheng nondui=new Nonduicheng();
	public static String sst=LoginListener.ST;
    public JSONObject RequestSS(String connectIp,int connectPort,int clientId,String clientIp,String ST,int num,String English){
        JSONObject js=null;
        try {
            Socket socket = new Socket(connectIp, connectPort);

            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);
            JSONObject Num = null;
            if(num==5) {
            	Num=MKfive(clientId,clientIp,connectIp,ST);
            }
            if(num==7) {
            	Num=MKseven(clientId,clientIp,connectIp);
            }
            if(num==10) {
            	Num=MKten(clientId,clientIp,connectIp,English,ST);
           }
            if(num==12) {
            	Num=MKtwelve(clientId,clientIp,connectIp,English);
           }
            if(num==14) {
            	Num=MKforteen(clientId,clientIp,connectIp,English);
           }
            if(num==16) {
              	Num=MKsixteen(clientId,clientIp,connectIp);
              }
            pw.write(Num.toString()+"\n");
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
    public JSONObject MKfive(int ClientId,String clientIp,String connectIp,String ST){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        send_time=df.format(new Date());

        JSONObject CSSauth = new JSONObject();
        CSSauth.put("Clientid",ClientId);
        CSSauth.put("Clientip",clientIp);
        CSSauth.put("CTtime",send_time);
        String CSSauth_String=CSSauth.toString();
        String CSSauth_Code=EncryUtil.encrypt(CSSauth_String,LoginListener.K_tgs_s);

        JSONObject message5 = new JSONObject();

        message5.put("CSSauth",CSSauth_Code);
        message5.put("ST",ST);


        JSONObject Num5=new JSONObject();
        Num5.put("msgType",5);
        Num5.put("ifError",0);
        Num5.put("sendIp",clientIp);
        Num5.put("receieveIp",connectIp);
        Num5.put("ifSign",0);
        Num5.put("message",message5);
        Num5.put("sign",message5);
 

        return Num5;
    }
    public JSONObject MKten(int ClientId,String clientIp,String connectIp,String Enword,String st){
       
    	JSONObject message10 = new JSONObject();
    	message10.put("Clientid", ClientId);
        message10.put("Enword",Enword);
        message10.put("ST",st);
        String message_string=message10.toString();
        System.out.println(message_string);
       // String message_code=nondui.jiami(message_string, "65537", "3430601959");//LoginListener.e2, LoginListener.n2
        String E2 = null;
    	String N2=null;   
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
   
        try (
            Connection c = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/net?characterEncoding=UTF-8",
                "root", "ljw092412");
            Statement s = c.createStatement();             
        )
        {
        	     	
        	String sql = "select * from server_keylist";           
            s.execute(sql);
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {              
                E2=rs.getString("E");
                N2=rs.getString("N");             
            }
               
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String message_code=nondui.jiami(message_string, E2, N2);
        System.out.println(LoginListener.n2);
        String info_code=message_code.substring(0, message_code.indexOf("*"));
        String sign_code=message_code.substring( message_code.indexOf("*")+1);
       // String message_deode=nondui.jiemi(message_code,  "65537", "3430601959");
       // System.out.println(message_deode);
        System.out.println(message_code);
        System.out.println(info_code+"*"+sign_code);
        IJFrame.getBaiduTranslate("自己的服务器票据："+st+"\n"+"明文是："+message_string+"\n"+"密文是："+message_code);
        
        JSONObject Num10=new JSONObject();
        Num10.put("type",IJFrame.type);
        Num10.put("id",ClientId);
        Num10.put("msgType",10);//消息类型
        Num10.put("ifError",0);
        Num10.put("sendIp",clientIp);
        Num10.put("receieveIp",connectIp);
        Num10.put("ifSign",1);//是否有数字签名
        Num10.put("message",info_code);//添加消息体
        Num10.put("sign",sign_code);//添加数字签名

        return Num10;
    }
    public JSONObject MKtwelve(int ClientId,String clientIp,String connectIp,String Ensentence){
        
    	JSONObject message12 = new JSONObject();
    	
    	message12.put("Clientid", ClientId);
        message12.put("Ensentence",Ensentence);
        
        JSONObject Num12=new JSONObject();
        Num12.put("msgType",12);//消息类型
        Num12.put("ifError",0);
        Num12.put("sendIp",clientIp);
        Num12.put("receieveIp",connectIp);
        Num12.put("ifSign",1);//是否有数字签名
        Num12.put("message",message12);//添加消息体
        Num12.put("sign",message12);//添加数字签名

        return Num12;
    }
    public JSONObject MKforteen(int ClientId,String clientIp,String connectIp,String Enwords){
        
    	JSONObject message14 = new JSONObject();
    	
    	message14.put("Clientid", ClientId);
        message14.put("Ensentence",Enwords);
        
        JSONObject Num14=new JSONObject();
        Num14.put("msgType",14);//消息类型
        Num14.put("ifError",0);
        Num14.put("sendIp",clientIp);
        Num14.put("receieveIp",connectIp);
        Num14.put("ifSign",1);//是否有数字签名
        Num14.put("message",message14);//添加消息体
        Num14.put("sign",message14);//添加数字签名

        return Num14;
    }
    public JSONObject MKsixteen(int ClientId,String clientIp,String connectIp){
    
	JSONObject message16 = new JSONObject();
	
	message16.put("Clientid", ClientId);
    
    JSONObject Num16=new JSONObject();
    Num16.put("msgType",16);//消息类型
    Num16.put("ifError",0);
    Num16.put("sendIp",clientIp);
    Num16.put("receieveIp",connectIp);
    Num16.put("ifSign",1);//是否有数字签名
    Num16.put("message",message16);//添加消息体
    Num16.put("sign",message16);//添加数字签名

    return Num16;
}
    public JSONObject MKseven(int ClientId,String clientIp,String connectIp){
        
    	JSONObject message7 = new JSONObject();
    	
    	message7.put("Clientid", ClientId);
    	message7.put("E", nondui.e1);//e
    	message7.put("N", nondui.n1);
    	message7.toString();
    	System.out.println(message7.toString());
    	String message7_Code=EncryUtil.encrypt(message7.toString(),LoginListener.K_tgs_s);
        
        JSONObject Num7=new JSONObject();
        Num7.put("id",ClientId);
        Num7.put("msgType",7);//消息类型
        Num7.put("ifError",0);
        Num7.put("sendIp",clientIp);
        Num7.put("receieveIp",connectIp);
        Num7.put("ifSign",1);//是否有数字签名
        Num7.put("message",message7_Code);//添加消息体
        Num7.put("sign",message7);//添加数字签名

        return Num7;
    }
}
