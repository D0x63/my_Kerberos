package as;

import Tool.EncryUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.RandomStringUtils;

import java.lang.Thread;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ASThread extends Thread {
    Socket socket = null;
    InetAddress inetAddress = null;
    ASSearch Search = new ASSearch();
    //String K_c_tgs = "abc";
    String K_c_tgs = RandomStringUtils.randomAlphanumeric(32);//随机产生一个对称密钥
    String K_tgs = "def";
    String asIp = "192.168.43.144";
    String tgsIp = "192.168.43.16";

    public ASThread(Socket socket, InetAddress inetAddress) {
        this.socket = socket;
        this.inetAddress = inetAddress;
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
                JSONObject Num1 = JSONObject.fromObject(info);
                JSONObject message1 = Num1.getJSONObject("message");

                //int ClientId=Integer.parseInt(message1.get("Clientid").toString());
                int ClientId = message1.getInt("Clientid");
                String CAtime = message1.getString("CAtime");

                String sql = "select * from get_id where user_id=" + ClientId;
                String ifError[] = Search.search(sql);
                InetAddress address = socket.getInetAddress();
                String sendIp = address.getHostAddress();

                //发送给客户端报文
                JSONObject Num2 = new JSONObject();
                Num2.put("msgType", 2);
                Num2.put("sendIp", asIp);
                Num2.put("receieveIp", sendIp);
                JSONObject message2 = new JSONObject();

                if (ifError[0].equals("false")) {
                    Num2.put("ifError", 1);
                    Num2.put("ifSign", 0);
                    message2.put("msgError", "ClientidError");
                    Num2.put("message", message2);
                    Num2.put("sign", "");
                } else {
                    String user_password = ifError[1]; //使用用户密码作为Kc
                    System.out.println(user_password);
                    Num2.put("ifError", 0);
                    Num2.put("ifSign", 1);

                    JSONObject TGT = new JSONObject();
                    TGT.put("Clientid", ClientId);
                    TGT.put("Clientip", sendIp);
                    TGT.put("TGSip", tgsIp);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    String str = df.format(new Date());
                    TGT.put("TGTstart", str);
                    TGT.put("TGTend", "2021-09-15 16:17:37");
                    TGT.put("K_tgs_c", K_c_tgs);
                   // String TGT_encode = Symmetrical_Encode.encode(TGT.toString(), K_tgs);
                    String TGT_encode= EncryUtil.encrypt(TGT.toString(), K_tgs);


                    message2.put("Clientid", ClientId);
                    message2.put("Clientip", sendIp);
                    message2.put("TGSip", tgsIp);
                    message2.put("TGTstart", str);
                    message2.put("TGTend", "2021-09-15 16:17:37");
                    message2.put("K_tgs_c", K_c_tgs);
                    message2.put("TGT", TGT_encode);
                    System.out.println(message2.toString());
                    String massage2_encode = EncryUtil.encrypt(message2.toString(), user_password);



                    Num2.put("message", massage2_encode);
                    Num2.put("sign", message2);
                }

                outputStream = socket.getOutputStream();
                writer = new OutputStreamWriter(outputStream, "UTF-8");
                writer.write(Num2.toString() + "\n");
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
