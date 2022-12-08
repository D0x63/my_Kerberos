package Net_Security.Translate_Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IJFrame extends JFrame {

    private JPanel contentPane;
     static String baiduTranslate;
    String get;
    static String type;
    static Connect_SS connect_ss=LoginListener.connect_ss;	
    static String serverIp=LoginListener.serverIp;
    static int clientId=LoginListener.clientId;
    static String clientIp=LoginListener.clientIp;
    static String ST=LoginListener.ST;
    static Nonduicheng tool=Connect_SS.nondui;
    JCheckBox boldCheckbox;
    JCheckBox italicCheckbox;
    public IJFrame() {
        setBackground(new Color(253, 245, 230));
        setAlwaysOnTop(true);
        setResizable(false);
        setTitle("翻译软件");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 975, 696);
        contentPane = new JPanel();
        contentPane.setBackground(SystemColor.menu);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel Tips = new JLabel("需要翻译的文本");
        Tips.setFont(new Font("华文仿宋", Font.PLAIN, 19));
        Tips.setBounds(14, 56, 167, 31);
        contentPane.add(Tips);

        JLabel Baidu = new JLabel("Baidu翻译");
        Baidu.setFont(new Font("华文仿宋", Font.PLAIN, 20));
        Baidu.setBounds(498, 69, 99, 18);
        contentPane.add(Baidu);
        
       
        JCheckBox checkBox01 = new JCheckBox("中文");
        JCheckBox checkBox02 = new JCheckBox("英文");
        JCheckBox checkBox03 = new JCheckBox("日文");
        checkBox01.setBounds(650,69,60,18);
        checkBox02.setBounds(710,69,60,18);
        checkBox03.setBounds(770,69,60,18);
        contentPane.add(checkBox01);
        contentPane.add(checkBox02);
        contentPane.add(checkBox03);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(14, 110, 369, 100);
        contentPane.add(scrollPane_1);

        final JTextArea GetText = new JTextArea();
        GetText.setLineWrap(true);
        scrollPane_1.setViewportView(GetText);
        GetText.setFont(new Font("华文仿宋",Font.PLAIN,20));

        JScrollPane scrollPane_2 = new JScrollPane();
        scrollPane_2.setBounds(538, 110, 388, 100);
        contentPane.add(scrollPane_2);

        final JTextArea BaiduArea = new JTextArea();
        BaiduArea.setEditable(false);
        BaiduArea.setLineWrap(true);
        scrollPane_2.setViewportView(BaiduArea);

        JButton btnReset = new JButton("Reset");
        btnReset.setToolTipText("复制翻译后的内容");
        btnReset.setForeground(Color.BLACK);
        btnReset.setFont(new Font("Calisto MT", Font.PLAIN, 15));
        btnReset.setBackground(SystemColor.menu);
        btnReset.setBounds(404, 160, 113, 27);
        contentPane.add(btnReset);

        JButton TranslateButton = new JButton("Translate");
        TranslateButton.setForeground(Color.BLACK);
        TranslateButton.setBackground(SystemColor.menu);
        TranslateButton.setFont(new Font("Calisto MT", Font.PLAIN, 15));
        TranslateButton.setBounds(404, 120, 113, 27);
        contentPane.add(TranslateButton);

        JLabel TimeLabel = new JLabel("New label");
        TimeLabel.setBounds(681, 626, 263, 18);
        contentPane.add(TimeLabel);
        Date nowTime = new Date();
        SimpleDateFormat date0 = new SimpleDateFormat("当前时间是：YY年MM月dd日 E");
        TimeLabel.setFont(new Font("华文仿宋", Font.PLAIN, 13));
        TimeLabel.setText(""+date0.format(nowTime));

        JLabel encode = new JLabel("请求信息密文");
        encode.setFont(new Font("华文仿宋", Font.PLAIN, 20));
        encode.setBounds(14, 220, 400, 40);
        contentPane.add(encode);

        JScrollPane scrollPane_3 = new JScrollPane();
        scrollPane_3.setBounds(14, 260, 930, 150);
        contentPane.add(scrollPane_3);

        JTextArea GetText2 = new JTextArea();
        GetText2.setLineWrap(true);
        scrollPane_3.setViewportView(GetText2);
        GetText2.setFont(new Font("华文仿宋",Font.PLAIN,20));

        JLabel encode2 = new JLabel("接收信息密文");
        encode2.setFont(new Font("华文仿宋", Font.PLAIN, 20));
        encode2.setBounds(14, 420, 400, 40);
        contentPane.add(encode2);

        JScrollPane scrollPane_4 = new JScrollPane();
        scrollPane_4.setBounds(14, 470, 930, 150);
        contentPane.add(scrollPane_4);

        JTextArea GetText3 = new JTextArea();
        GetText3.setLineWrap(true);
        scrollPane_4.setViewportView(GetText3);
        GetText3.setFont(new Font("华文仿宋",Font.PLAIN,20));

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GetText.setText(null);
                BaiduArea.setText(null);
            }
        });
        //���ӷ��밴ť�¼�
        TranslateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                get = GetText.getText();
                //String uigettext = null;
            	JSONObject Msg;
            	
            		System.out.println("翻译单词");
            		String E2 = null;
                	String N2=null;   
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                    } catch (ClassNotFoundException e1) {
                        e1.printStackTrace();
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
                           
                    } catch (SQLException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
            		Msg=connect_ss.RequestSS(serverIp,10066,clientId,clientIp,ST,10,get);
            		System.out.println("clieny"+Msg);
            		//String chword=Msg.getString("Transword");LoginListener.e2, LoginListener.n2
            		String code=Msg.getString("message")+"*"+Msg.getString("sign");
            		String info=tool.jiemi(code, E2, N2);
            		String flag=info.substring(0, info.indexOf("*"));
            		String trans=info.substring(info.indexOf("*")+1);
            		JSONObject message10=JSONObject.fromObject(trans);
            		String trans_word=message10.getString("Transword");
            		if(flag.equals("true"))
            		{
            			BaiduArea.setText(trans_word);
            			GetText2.setText(baiduTranslate);
            		}
            		else
            		{
            			BaiduArea.setText("wrong");
            		}
            		String test3="明文是："+info+"\n"+"密文是："+code;
            		GetText3.setText(test3);
            	
            	
                System.out.println(get);
            }
        });
        ActionListener actionListener = new ActionListener(){
            public void actionPerformed(ActionEvent e) {
              int mode = 0;
              if(checkBox01.isSelected()) type="1";
              if(checkBox02.isSelected()) type="2";  
              if(checkBox03.isSelected()) type="3"; 
             
            }
          };              
       checkBox01.addActionListener(actionListener);   
       checkBox02.addActionListener(actionListener);  
       checkBox03.addActionListener(actionListener);                                
    }
    public static void getBaiduTranslate(String Baidutranslate)
    {
        baiduTranslate = Baidutranslate;
    }
}


