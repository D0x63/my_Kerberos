package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class jdbc {
	 static Connection con = null;
	    static Statement statement = null;
	    static ResultSet rs = null;
	    static String driver = "com.mysql.jdbc.Driver";
	    static String url = "jdbc:mysql://127.0.0.1:3306/net?characterEncoding=UTF-8";
	    static String user = "root";
	    static String password = "ljw092412";
	    public static void main(String[] args)
	    {
	    	try {
				Class.forName(driver);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           
          
            String sql = "select * from herotest where server_id=1";
            try {
            	con = DriverManager.getConnection(url,user,password);
            	statement = con.createStatement();
				rs = statement.executeQuery(sql);
				int server_id =0; 
	            String sttime="",edtime="";
	            String stime="2015",etime="2017",ST="qwert";
	            String sql_one = "select * from herotest where server_id=1";
	            rs = statement.executeQuery(sql);
	            while(rs.next())
	            {
	                server_id=rs.getInt("server_id");
	                sttime=rs.getString("sttime");
	                edtime=rs.getString("edtime");
	                System.out.println(server_id);
	            }
	            if(server_id!=0)
	            {
	           	 String sql_two = "update herotest set sttime = '"+stime+"' where server_id =1";
	                statement.execute(sql_two);
	                String sql_three="update herotest set edtime ='"+ etime+"' where server_id =1";
	                statement.execute(sql_three);
	                String sql_five="update herotest set st ='"+ ST+"' where server_id =1";
	                statement.execute(sql_five);
	            }
	            else
	            {
	           	 System.out.println(".........................................................."+ST);
	           	 String sql_four = "insert into herotest values(1,'"+(1+stime)+"','"+etime+"','"+ ST+"')";
	           	 statement.execute(sql_four);
	            }
	            
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
	    }

}
