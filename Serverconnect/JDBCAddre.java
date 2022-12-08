package Serverconnect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

//加搜索记录的类
public class JDBCAddre {
        public void addrecord(String sql) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try (
                    Connection c = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/serverdatabase?characterEncoding=UTF-8",
                            "root", "admin");
                    Statement s = c.createStatement();
            )
            {
                s.execute(sql);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
