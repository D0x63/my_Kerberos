package as;

import java.sql.*;
import Tool.Symmetrical_Encode;

public class ASSearch {
    public ASSearch() { }
    public String [] search(String sql) {
        Connection con = null;
        Statement statement = null;
        ResultSet rs = null;
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://127.0.0.1:3306/test?characterEncoding=UTF-8";
        String user = "root";
        String password = "710382941";
        String result[]=new String[2];
        try
        {
            Class.forName(driver);
            con = DriverManager.getConnection(url,user,password);
            statement = con.createStatement();
            rs = statement.executeQuery(sql);

            int client_id = 0;
            if(rs.next()) {
                client_id = rs.getInt("user_id");
        }
            if(client_id==0) result[0]="false";
            else {
                result[0] = "true";
                result[1]=rs.getString("user_password");
            }
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // 从内向外释放连接
            try {
                if(null != rs) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if(null != statement) {
                    statement.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if(null != con) {
                    con.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
