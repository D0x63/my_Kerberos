package Net_Security.Translate_Client;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginListener implements ActionListener{
	 private JTextField text_name;
	    private JPasswordField text_password;
	    private JFrame login;
	    private int client_id;
	    static Nonsymmetrical_Encode Rsa_Encode;
	    static Connect_AS connect_as=new Connect_AS();
	    static Connect_TGS connect_tgs=new Connect_TGS();
	    static Connect_SS connect_ss=new Connect_SS();	
	    static Connection con = null;
	    static Statement statement = null;
	    static ResultSet rs = null;
	    static String driver = "com.mysql.jdbc.Driver";
	    static String url = "jdbc:mysql://127.0.0.1:3306/net?characterEncoding=UTF-8";
	    static String user = "root";
	    static String password = "ljw092412";
	    static String serverIp="192.168.43.182";
	    static String ASIp="192.168.43.144";
	    static String TGSIp="192.168.43.16";
	    static int clientId;
	    static String clientIp="192.168.43.178";
	    static int tgsId=4;
	    static String ST;
	    static String K_tgs_c;
	    static String K_tgs_s;
	    static String e2;
	    static String n2;
	    
	    public LoginListener(JFrame login, JTextField text_name, JPasswordField text_password)
	    {//获取登录界面、账号密码输入框对象
	        this.login=login;
	        this.text_name=text_name;
	        this.text_password=text_password;
	    }

    
	    public void actionPerformed(ActionEvent e) {             	
         try {       	       	
            //连接数据库，通信前判断是否有证书，并判断是否过期
            Class.forName(driver);
            con = DriverManager.getConnection(url,user,password);
            statement = con.createStatement();
           
            clientId=Integer.parseInt(text_name.getText());
            String client_password=text_password.getText();
            String sql = "select * from ST where client_id="+clientId;
            rs = statement.executeQuery(sql);
            int client_id =0; 
            String sttime="",edtime="";
            while(rs.next())
            {
            	client_id=rs.getInt("client_id");
                sttime=rs.getString("sttime");
                edtime=rs.getString("edtime");
            }
            int tmp=1;
            if(client_id!=0){
                SimpleDateFormat getTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                String Time=getTime.format(new Date());
                for(int i=0;i<Time.length();i++){
                    if(Character.isDigit(Time.charAt(i))){
                        if(Time.charAt(i)<edtime.charAt(i)) break;
                        else if(Time.charAt(i)>edtime.charAt(i)){
                            tmp=0;
                            break;
                        }
                    }
                }
            }

            //没有票或票过期，直接访问AS
            if(client_id==0||tmp==0){
                int ifTicket=0;
                //循环到可以正常访问SS
              
                    //访问AS
                    JSONObject Msg=null;
                    Msg=connect_as.RequestAS(ASIp,10068,clientId,tgsId,clientIp);
                    int ifError=0;
                    ifError=Msg.getInt("ifError");
                    if(ifError==1){  //AS报错的话重新输入账号密码
                        System.out.println("账号或密码错误");
                        errorUI();                       
                    }
                    else
                    {
                    	 //JSONObject TGT=Msg.getJSONObject("message").getJSONObject("TGT");
                        System.out.println(Msg);
                        String Code_Message2=Msg.getString("message");
                        System.out.println(EncryUtil.decrypt(Code_Message2,client_password));
                        JSONObject Message2=JSONObject.fromObject(EncryUtil.decrypt(Code_Message2,client_password));
                        if(Message2.size()==0){
                            System.out.println("账号或密码错误");
                            errorUI();
                        }
                        else
                        {
                        	String TGT=Message2.getString("TGT");
                            K_tgs_c=Message2.getString("K_tgs_c");
                            System.out.print(K_tgs_c);
                            System.out.print(" AS success");
                            
                            
                            //访问TGS
                            Msg=connect_tgs.RequestTGS(TGSIp,10067,clientId,serverIp,TGT,clientIp);
                            System.out.println(Msg.toString());
                            ifError=Msg.getInt("ifError");
                            int tagTGS=1;
                            if(ifError==1){
                                String message= Msg.getString("message");
                                if(message.equals("ClientidError")||message.equals("ClientipError")){
                                    System.out.println("账号或密码错误");//提示重新登陆
                                    errorUI();
                                    
                                }
                               
                            }
                            
                          
                            String Code_Message4=Msg.getString("message");
                            System.out.println(Code_Message4);
                            JSONObject Message4=JSONObject.fromObject(EncryUtil.decrypt(Code_Message4,K_tgs_c));
                            System.out.println(Message4);
                                   ST=Message4.getString("ST");
                                   
                                   K_tgs_s=Message4.getString("K_c_ss");
                            String stime=Message4.getString("STstart");
                            String etime=Message4.getString("STend");
                            //添加删除条目
                          
                            
                            String sql_one = "select * from ST where client_id="+clientId;                                     
                	            rs = statement.executeQuery(sql_one);
                	            while(rs.next())
                	            {
                	            	client_id=rs.getInt("client_id");        	               
                	                System.out.println(client_id);
                	            }
                	            if(client_id==clientId)
                	            {
                	            	System.out.println(etime);
                	           	 	String sql_two = "update ST set sttime = '"+stime+"' where client_id ="+clientId;
                	                statement.execute(sql_two);
                	                //String sql_three="update ST set edtime ='"+ etime+"' where server_id ="+clientId;
                	                String sql_three="update ST set edtime ='"+ etime+"' where client_id ="+clientId;
                	                statement.execute(sql_three);
                	                String sql_five="update ST set st ='"+ ST+"' where client_id ="+clientId;
                	                statement.execute(sql_five);
                	            }
                	            else
                	            {
                	           	 System.out.println(".........................................................."+ST);
                	           	 String sql_four = "insert into ST values(1,'"+clientId+"','"+stime+"','"+etime+"','"+ ST+"','"+client_password+"')";
                	           	 statement.execute(sql_four);
                	            }
                	            
                			
                            System.out.print(" TGS success");
                            
                            //访问SS
                            boolean SStime_Auth=true;
                            while(SStime_Auth)
                            {
                            	
                            	Msg=connect_ss.RequestSS(serverIp,10066,clientId,clientIp,ST,5,null);
                                ifError=Msg.getInt("ifError");
                                int tagSS=1;
                                while(ifError==1){
                                    String message= Msg.getString("message");
                                    if(message.equals("ClientidError")||message.equals("ClientipError")){
                                        System.out.println("账号或密码错误");//提示重新登陆
                                        errorUI();
                                        Msg=connect_ss.RequestSS(serverIp,10068,clientId,clientIp,ST,5,null);
                                        ifError=Msg.getInt("ifError");
                                    }
                                    else{
                                        tagSS=0;
                                        break;
                                    }
                                }
                                if(tagSS!=0) ifTicket=1;
                                System.out.println(Msg);
                                String Code_Message6=Msg.getString("message");
                                System.out.println();
                                JSONObject Message6=JSONObject.fromObject(EncryUtil.decrypt(Code_Message6,K_tgs_s));
                                String Auth_Time=Message6.getString("STtime");
                                String CLient_Time=Connect_SS.send_time;
                                if((CLient_Time+"+1").equals(Auth_Time))
                                {
                                	System.out.println("客户端认证服务器成功");
                                	Msg=connect_ss.RequestSS(serverIp,10066,clientId,clientIp,ST,7,null);
                                	System.out.println(Msg);
                                	String message8_code=Msg.getString("message");
                                	JSONObject message8=JSONObject.fromObject(EncryUtil.decrypt(message8_code,LoginListener.K_tgs_s));
                                	e2=message8.getString("pubkeyE");
                                	n2=message8.getString("pubkeyN");
                                	System.out.println(e2+"...."+n2);
                                	String sql_serverkey_one = "select * from server_keylist where server_id=1";
                                    int server_id = 0;                        	
                    	            rs = statement.executeQuery(sql_serverkey_one);
                    	            while(rs.next())
                    	            {
                    	                server_id=rs.getInt("server_id");        	               
                    	                System.out.println(server_id);
                    	            }
                    	            if(server_id!=0)
                    	            {
                    	           	 String sql_serverkey_two = "update server_keylist set E = '"+e2+"' where server_id =1";
                    	                statement.execute(sql_serverkey_two);
                    	                String sql_serverkey_three="update server_keylist set N ='"+ n2+"' where server_id =1";
                    	                statement.execute(sql_serverkey_three);            	                
                    	            }
                    	            else
                    	            {
                    	           	 System.out.println(".........................................................."+ST);
                    	           	 String sql_serverkey_four = "insert into server_keylist values(1,'"+e2+"','"+n2+"')";
                    	           	 statement.execute(sql_serverkey_four);
                    	            }
                                	break;
                                }
                                else
                                {
                                	System.out.println("重新登陆，已经得到票据");
                                	SStime_Auth=false;
                                }
                            }
                        
                        
                        System.out.print(" SS success");
                        transUI();
                        //System.out.println(Msg.toString());
                        }
                        
                }
            }
            else{
            	 rs = statement.executeQuery(sql);
                 int tt=0;
                 String password_true = null;
                 while(rs.next())
                 {
                     ST=rs.getString("st");
                     tt=rs.getInt("client_id");
                     password_true=rs.getString("password");
                 }
                 if(tt!=clientId||!password_true.equals(client_password)){
                     errorUI();
                 }
                 else{
                     System.out.println("可以访问server");
                     transUI();
                 }
            }
        }
         catch(ClassNotFoundException ee) {
             ee.printStackTrace();
         }
         catch(SQLException ee) {
             ee.printStackTrace();
         }
         catch (Exception ee) {
             ee.printStackTrace();
         }
        finally {
        	 try {
                 if(null != rs) {
                     rs.close();
                 }
             }
             catch (SQLException ee) {
                 ee.printStackTrace();
             }
             try {
                 if(null != statement) {
                     statement.close();
                 }
             }
             catch (SQLException ee) {
                 ee.printStackTrace();
             }
             try {
                 if(null != con) {
                     con.close();
                 }
             }
             catch (SQLException ee) {
                 ee.printStackTrace();
             }
         }
        }
    
    public static boolean check(String s) {
        boolean b = false;

        String tmp = s;
        tmp = tmp.replaceAll("\\p{P}", "");
        if (s.length() != tmp.length()) {
            b = true;
        }

        return b;
    }

public void transUI(){
    EventQueue.invokeLater(new Runnable() {
        public void run() {
            try {
            	IJFrame  frame = new IJFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

}
public void errorUI(){
    Dimension dim2 = new Dimension(100,30);
    Dimension dim3 = new Dimension(300,30);

    //生成新界面
    final JFrame login2 = new javax.swing.JFrame();
    login2.setSize(400,200);
    login2.setDefaultCloseOperation(3);
    login2.setLocationRelativeTo(null);
    login2.setFont(new Font("宋体",Font.PLAIN,14));  //宋体，正常风格，14号字体
    //创建组件
    javax.swing.JPanel jp1 = new JPanel();
    javax.swing.JPanel jp2 = new JPanel();

    JLabel message = new JLabel("账号或密码错误");
    message.setFont(new Font("宋体",Font.PLAIN,14));  //宋体，正常风格，14号字体
    message.setPreferredSize(dim3);
    //将textName标签添加到窗体上
    jp1.add(message);
    login2.add(jp1,BorderLayout.CENTER);

    JButton close = new JButton("确定");
    close.setFont(new Font("宋体",Font.PLAIN,14));
    //设置按键大小
    close.setSize(dim3);
    jp2.add(close);
    login2.add(jp2,BorderLayout.SOUTH);

    close.addActionListener(new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            login2.dispose();
        }
    });

    login2.setResizable(false);
    login2.setVisible(true);
}

}
