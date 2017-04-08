package cn.mvncode.webcrawler.Utils;

import cn.mvncode.webcrawler.Processor.DataBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 为外界提供数据库连接对象
 * Created by Pavilion on 2017/4/8.
 */
public class DataBaseUtil {

    private static Logger logger = LoggerFactory.getLogger(DataBase.class.getName());

    private static String driver = "com.mysql.jdbc.Drive";
    private static String url = "jdbc:mysql://127.0.0.1:3306/moviebase?" +
            "useUnicode=true&characterEncoding=UTF-8";
    private static String user = "root";
    private static String pwd = "Pavilion5556";

    private static Connection connection = null;//声明数据库连接对象

    /**
     * 加载驱动
     */
    static {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
            logger.error("load driver failed");
        }
    }

    /**
     * 返回数据库连接对象
     *
     * @return
     * @throws SQLException
     */
    public static Connection getConnection () throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection(url, user, pwd);
            connection.setAutoCommit(false);//设置手动提交
            return connection;
        }
        return connection;
    }

    /**
     * 测试连接是否成功
     */
    public static boolean isConnection (Connection conn) {
        if (connection != null) {
            logger.info("DataBase connect to normal");
            return true;
        } else {
            logger.error("DataBase connect to fail");
            return false;
        }
    }

}
