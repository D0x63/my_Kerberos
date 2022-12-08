package Serverconnect;

import java.net.InetAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//连接数据库操作
public class SSearch {
    public SSearch() {

    }
//数据库查找id是否存在操作
    public int search(String sql)
    {
        Connection con = null;
        Statement statement = null;
        ResultSet rs = null;
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://127.0.0.1:3306/serverdatabase?characterEncoding=UTF-8";
        String user = "root";
        String password = "admin";
        try
        {
            Class.forName(driver);
            // 使用DriverManager获取连接
            con = DriverManager.getConnection(url,user,password);
            // 创建statement类对象，用来执行SQL语句！！
            statement = con.createStatement();
            rs = statement.executeQuery(sql);

            int client_id = 0;
            while(rs.next())
            {
                client_id = rs.getInt("client_id");
            }
            if(client_id==0) return 1;
            else return 0;
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            // 从内向外释放连接
            try
            {
                if(null != rs)
                {
                    rs.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            try
            {
                if(null != statement)
                {
                    statement.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            try
            {
                if(null != con)
                {
                    con.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
