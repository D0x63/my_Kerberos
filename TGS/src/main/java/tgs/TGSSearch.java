package tgs;

import java.net.InetAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TGSSearch {
    public TGSSearch() {

    }
    public int search(String sql)
    {
        Connection con = null;
        Statement statement = null;
        ResultSet rs = null;
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/TEST_TGS?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "root";
        String password = "123456";
        try
        {
            Class.forName(driver);
            // 使用DriverManager获取连接
            con = DriverManager.getConnection(url,user,password);
            // 创建statement类对象，用来执行SQL语句！！
            statement = con.createStatement();
            rs = statement.executeQuery(sql);

            String http_ip = null;
            while(rs.next())
            {
                http_ip = rs.getString("server_ip");
            }
            if(http_ip==null) return 1;
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
