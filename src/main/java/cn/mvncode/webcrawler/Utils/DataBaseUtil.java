package cn.mvncode.webcrawler.Utils;

import cn.mvncode.webcrawler.Processor.CommentDataBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 为外界提供数据库连接对象
 * Created by Pavilion on 2017/4/8.
 */
public class DataBaseUtil {

    private static Logger logger = LoggerFactory.getLogger(CommentDataBase.class.getName());

    private static String driver = "com.mysql.jdbc.Drive";
    private static String url = "jdbc:mysql://127.0.0.1:3306/moviebase?" +
            "useUnicode=true&characterEncoding=UTF-8";
    private static String user = "root";
    private static String pwd = "Pavilion5556";

    private static Connection connection;//声明数据库连接对象
    private static PreparedStatement preparedStatement;//处理字段

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
            logger.info("CommentDataBase connect to normal");
            return true;
        } else {
            logger.error("CommentDataBase connect to fail");
            return false;
        }
    }

    /**
     * 创建表
     *
     * @param conn
     */
    public static void createTable (Connection conn, String sql) {
        try {
            preparedStatement = conn.prepareStatement(sql);//预处理
            preparedStatement.executeUpdate(sql);//执行
            preparedStatement.close();
        } catch (SQLException e) {
//            e.printStackTrace();
            logger.error("create table failed");
        }
    }

    /**
     * 判断表是否存在
     *
     * @return
     */
    public static boolean exitTable (String tableName, Connection conn) {
        String sql = "SHOW TABLES LIKE '" + tableName + "';";
        boolean flag = false;
        try {
            preparedStatement = conn.prepareStatement(sql);
            flag = preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
//            e.printStackTrace();
            logger.error("sql fetch failed");
            return flag;
        }
        return flag;
    }

    /**
     * 添加数据
     *
     * @throws SQLException
     */
    public static void insert (Connection conn, String tableName, String[] columns, List<String[]> data) {
        List<String> sqls = new ArrayList<>();
        //insert into tablename (columns) values (value1,value2,...);
        for (int i = 0; i < data.size(); i++) {
            StringBuffer sql = new StringBuffer("INSERT IGNORE INTO " + tableName + "(");
            String[] oneData = data.get(i);
            for (int j = 0; j < columns.length; j++) {
                sql.append(columns[j]);
                if (j < columns.length - 1) {//防止最后一个
                    sql.append(",");
                }
            }
            sql.append(")").append(" VALUES (");
            for (int k = 0; k < oneData.length; k++) {
                sql.append(oneData[k]);
                if (k < oneData.length - 1) {
                    sql.append(",");
                }
            }
            sql.append(");");
            sqls.add(sql.toString());
        }
        //执行(批量)
        try {
            for (int i = 0; i < sqls.size(); i++) {
                //预处理SQL,防止注入
                preparedStatement = conn.prepareStatement(sqls.get(i));
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            conn.commit();//提交
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
//            e.printStackTrace();
            logger.error("add data field");
        }
    }

    /**
     * 查询表(未完成)
     *
     * @param conn
     * @param tableName
     * @param column
     * @param data
     * @return
     */
    public static List<String> query (Connection conn, String tableName, String column, String data) {
        //select * from tablename where column = 'data'
        List<String> list = new ArrayList();
        String sql = "SELECT * FROM " + tableName + "WHERE " + column + "='" + data + "';";
//        executePs(conn, sql, 1, );
        return list;
    }


}
