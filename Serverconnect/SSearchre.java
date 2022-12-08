package Serverconnect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

//查找聊天记录
public class SSearchre {
ArrayList<String> recordList=new ArrayList<String>();
    public ArrayList<String> searchrecord(String sql){ {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try (Connection c = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/serverdatabase?characterEncoding=UTF-8",
                    "root", "admin");
                 Statement s = c.createStatement();) {

                // 执行查询语句，并把结果集返回给ResultSet
                ResultSet rs = s.executeQuery(sql);
                while (rs.next()) {
                    recordList.add(rs.getString("record"));
                }
                // 不一定要在这里关闭ReultSet，因为Statement关闭的时候，会自动关闭ResultSet
                // rs.close();

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return  recordList;
    }
}
