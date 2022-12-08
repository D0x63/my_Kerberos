package Serverconnect;

import baidu.translate.demo.transmain;
import net.sf.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
public class SSThread extends Thread{
    Socket socket = null;
    InetAddress inetAddress=null;
    SSearch Search = new SSearch();
    JDBCAdd jdbcAdd=new JDBCAdd();

    public SSThread(Socket socket, InetAddress inetAddress) {
        this.socket = socket;
        this.inetAddress=inetAddress;
    }

    @Override
    public void run(){
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;//字符流
        BufferedReader bufferedReader = null;
        OutputStream outputStream = null;
        OutputStreamWriter writer = null;
        try {
            inputStream = socket.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String info = null;
            //接收客户端发送的报文
            while ((info = bufferedReader.readLine()) != null) {
                JSONObject Message=JSONObject.fromObject(info);//json转java
                //解析json格式的报文，得到报文的类型
                int msgType=Message.getInt("msgType");
                //如果接收到的报文类型时报文五
                if(msgType==5){
                    JSONObject message5=Message.getJSONObject("message");
                    JSONObject ST=message5.getJSONObject("ST");
                    JSONObject CSSauth=message5.getJSONObject("CSSauth");
                    int Clientid_ST=Integer.parseInt(ST.get("Clientid").toString());//ST中client端id
                    String Clientip_ST=ST.get("Clientip").toString();//ST中client端ip
                    String SSip=ST.get("SSip").toString();//ST中server端ip
                    String STstart=ST.get("STstart").toString();//ST中端生效时间
                    String STend=ST.get("STend").toString();//ST中失效时间
                    String K_c_ss=ST.get("K_c_ss").toString();//ST中client和server的对称密钥
                    //解析CSSauth的内容
                    int Clientid_CSSauth=Integer.parseInt(CSSauth.get("Clientid").toString());
                    String Clientip_CSSauth=CSSauth.get("Clientip").toString();
                    String CTtime=CSSauth.get("CTtime").toString();
                    //准备回复给客户端的报文六
                    JSONObject Num6 = new JSONObject();
                    Num6.put("msgType",6);//设置报文类型
                    Num6.put("sendIp",SSip);//设置发送方ip
                    Num6.put("receieveIp",Clientip_ST);//设置接收方ip
                    JSONObject message6 = new JSONObject();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    String str=df.format(new Date());
                    int tag=1;
                    for(int i=0;i<str.length();i++){
                        if(Character.isDigit(str.charAt(i))){
                            if(str.charAt(i)<STend.charAt(i)) break;
                            else if(str.charAt(i)>STend.charAt(i)){
                                tag=0;
                                break;
                            }
                        }
                    }

                    if(Clientid_ST!=Clientid_CSSauth||!Clientip_ST.equals(Clientip_CSSauth)||tag==0){
                        Num6.put("ifError",1);
                        Num6.put("ifSign",0);
                        String tmp="";
                        if(Clientid_ST!=Clientid_CSSauth)
                            tmp+="ClientidError";
                        if(!Clientip_ST.equals(Clientip_CSSauth))
                            tmp+="+ClientipError";
                        if(tag==0)
                            tmp+="+TGTError";
                        message6.put("msgError",tmp);
                        Num6.put("message",message6);
                        Num6.put("sign","");
                    }
                    else{
                        Num6.put("ifError",0);
                        Num6.put("ifSign",1);

                        message6.put("SSip",SSip);
                        message6.put("STtime",CTtime+"+1");

                        Num6.put("message",message6);
                        Num6.put("sign",message6);
                        //"insert into hero values(null," + "'提莫'" + "," + 313.0f + "," + 50 + ")"
                        String sql="insert into clientlist values(null,Clientid_ST)";
                        jdbcAdd.add(sql);
                    }
                    outputStream = socket.getOutputStream();
                    writer = new OutputStreamWriter(outputStream, "UTF-8");
                    writer.write(Num6.toString()+"\n");
                    writer.flush();
                }
                //客户端请求英文单词翻译，服务器回复
              if(msgType==10){
                  String sendIp=Message.getString("sendIp");
                  String reveiveIp=Message.getString("receieveIp");
                  JSONObject message10=Message.getJSONObject("message");
                  int Clientid=message10.getInt("Clientid");//解析客户端的Clientid;
                  String enword=message10.getString("Enword");//解析客户端的翻译文段
                  //查找此客户端ID是否已经在数据库中，即是否已经进行报文五六的相互认证
                  String sql="select * from clientlist where clientid="+Clientid;
                  int tag=Search.search(sql);
                  if(tag==1){
                      //此ID已经进行过认证
                      //将记录加入到数据库中
                      String intersql="insert into clientrecord values(null,'"+Clientid+"','"+sendIp+"','"+enword+"')";
                      JDBCAddre jdbcAddre=new JDBCAddre();
                      jdbcAddre.addrecord(intersql);
                      transmain transmain=new transmain();
                      String Transword=transmain.translate(enword);
                      JSONObject Num11 = new JSONObject();
                      Num11.put("msgType",11);//设置报文类型
                      Num11.put("sendIp",reveiveIp);//设置发送方ip
                      Num11.put("receieveIp",sendIp);//设置接收方ip
                      JSONObject message11 = new JSONObject();
                      Num11.put("Clientid",Clientid);
                      Num11.put("Transword",Transword);
                      outputStream = socket.getOutputStream();
                      writer = new OutputStreamWriter(outputStream, "UTF-8");
                      writer.write(Num11.toString()+"\n");
                      writer.flush();
                  }
              }
                //客户端请求英文句子翻译，服务器回复
                if(msgType==12){
                    String sendIp=Message.getString("sendIp");
                    String reveiveIp=Message.getString("receieveIp");
                    JSONObject message12=Message.getJSONObject("message");
                    int Clientid=message12.getInt("Clientid");//解析客户端的Clientid;
                    String ensentence=message12.getString("Ensentence");//解析客户端的翻译文段
                    //查找此客户端ID是否已经在数据库中，即是否已经进行报文五六的相互认证
                    String sql="select * from clientlist where clientid="+Clientid;
                    int tag=Search.search(sql);
                    if(tag==1){
                        //此ID已经进行过认证
                        String intersql="insert into clientrecord values(null,'"+Clientid+"','"+sendIp+"','"+ensentence+"')";
                        JDBCAddre jdbcAddre=new JDBCAddre();
                        jdbcAddre.addrecord(intersql);
                        transmain transmain=new transmain();
                        String Transsentence=transmain.translate(ensentence);
                        JSONObject Num13 = new JSONObject();
                        Num13.put("msgType",13);//设置报文类型
                        Num13.put("sendIp",reveiveIp);//设置发送方ip
                        Num13.put("receieveIp",sendIp);//设置接收方ip
                        JSONObject message13 = new JSONObject();
                        Num13.put("Clientid",Clientid);
                        Num13.put("Transsentence",Transsentence);
                        outputStream = socket.getOutputStream();
                        writer = new OutputStreamWriter(outputStream, "UTF-8");
                        writer.write(Num13.toString()+"\n");
                        writer.flush();
                    }
                }
                //客户端请求英文多个单词翻译，服务器回复
                if(msgType==14){
                    String sendIp=Message.getString("sendIp");
                    String reveiveIp=Message.getString("receieveIp");
                    JSONObject message14=Message.getJSONObject("message");
                    int Clientid=message14.getInt("Clientid");//解析客户端的Clientid;
                    String enwords=message14.getString("Enwords");//解析客户端的翻译文段
                    //查找此客户端ID是否已经在数据库中，即是否已经进行报文五六的相互认证
                    String sql="select * from clientlist where clientid="+Clientid;
                    int tag=Search.search(sql);
                    if(tag==1){
                        //此ID已经进行过认证
                        String intersql="insert into clientrecord values(null,'"+Clientid+"','"+sendIp+"','"+enwords+"')";
                        JDBCAddre jdbcAddre=new JDBCAddre();
                        jdbcAddre.addrecord(intersql);
                        transmain transmain=new transmain();
                        String Transwords=transmain.translate(enwords);
                        JSONObject Num15 = new JSONObject();
                        Num15.put("msgType",15);//设置报文类型
                        Num15.put("sendIp",reveiveIp);//设置发送方ip
                        Num15.put("receieveIp",sendIp);//设置接收方ip
                        JSONObject message15 = new JSONObject();
                        Num15.put("Clientid",Clientid);
                        Num15.put("Transwords",Transwords);
                        outputStream = socket.getOutputStream();
                        writer = new OutputStreamWriter(outputStream, "UTF-8");
                        writer.write(Num15.toString()+"\n");
                        writer.flush();
                    }
                }
                //客户端请求翻译记录，服务器回复
                if(msgType==16){
                    String sendIp=Message.getString("sendIp");
                    String reveiveIp=Message.getString("receieveIp");
                    JSONObject message16=Message.getJSONObject("message");
                    int Clientid=message16.getInt("Clientid");//解析客户端的Clientid;
                    //查找此客户端ID是否已经在数据库中，即是否已经进行报文五六的相互认证
                    String sql="select * from clientlist where clientid="+Clientid;
                    int tag=Search.search(sql);
                    if(tag==1){
                        //查找翻译记录表中值为Clientid
                        String intersql="select * from clientrecord where clientid="+Clientid;
                        ArrayList<String> recordArrayList=new SSearchre().searchrecord(intersql);
                        //此ID已经进行过认证
                        transmain transmain=new transmain();
                        JSONObject Num17 = new JSONObject();
                        Num17.put("msgType",17);//设置报文类型
                        Num17.put("sendIp",reveiveIp);//设置发送方ip
                        Num17.put("receieveIp",sendIp);//设置接收方ip
                        JSONObject message17 = new JSONObject();
                        Num17.put("Clientid",Clientid);
                        Num17.put("Transwords",recordArrayList);
                        outputStream = socket.getOutputStream();
                        writer = new OutputStreamWriter(outputStream, "UTF-8");
                        writer.write(Num17.toString()+"\n");
                        writer.flush();
                    }
                }


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
