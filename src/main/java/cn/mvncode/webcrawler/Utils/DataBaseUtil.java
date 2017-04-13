package cn.mvncode.webcrawler.Utils;

import cn.mvncode.webcrawler.Processor.CommentDataBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * 为外界提供数据库连接对象
 * Created by Pavilion on 2017/4/8.
 */
public class DataBaseUtil {

    private static Logger logger = LoggerFactory.getLogger(CommentDataBase.class.getName());

    private static String driver = "com.mysql.cj.jdbc.Driver";

//    private static Connection conn;//声明数据库连接对象
//    private static PreparedStatement preparedStatement;//处理字段

    /**
     * 加载驱动
     */
    static {
        try {
            Class.forName(driver);
            logger.info("load driver to normal");
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
    public static Connection getConnection (String baseName) {
        Connection connection = null;
        String url = "jdbc:mysql://127.0.0.1:3306/" + baseName
                + "?useUnicode=true&characterEncoding=UTF-8&useSSL=true";
        String user = "root";
        String pwd = "Pavilion5556";
        try {
            connection = DriverManager.getConnection(url, user, pwd);
            connection.setAutoCommit(false);//设置手动提交
        } catch (SQLException e) {
            logger.error("connect failed\t" + e.getMessage());
        }
        return connection;
    }

    /**
     * 测试连接是否成功
     */
    public static boolean isConnection (Connection conn) {
        if (conn != null) {
            return true;
        } else {
            logger.error("CommentDataBase connect to fail");
            return false;
        }
    }

    /**
     * 创建表
     */
    public static void createTable (String baseName, String sql) {

        Connection conn = getConnection(baseName);
        if (!isConnection(conn)) {
            logger.debug("reconnecting...");
            return;
        } else {
            try {
                PreparedStatement preparedStatement = conn.prepareStatement(sql);//预处理
                if (preparedStatement.execute(sql)) {
                    preparedStatement.executeUpdate(sql);//执行
                }
                preparedStatement.close();
            } catch (SQLException e) {
                logger.error("create table failed:\t" + e.getMessage());
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断表是否存在
     *
     * @return
     */
    public static boolean exitTable (String baseName, String tableName) {
        Connection conn = getConnection(baseName);
        boolean flag = false;
        if (!isConnection(conn)) {
            logger.warn("reconnecting...");
            return flag;
        } else {
            try {
                //创建连接Connection
                //通过连接获取DatabaseMetaData，即调用connection.getMetaData()
                //调用DatabaseMetaData的getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)方法，各个参数的意义可参考API说明；该方法返回ResultSet
                //通过调用上文中返回结果ResultSet的方法next()，若返回true则表示存在该表 反之不存在。
                DatabaseMetaData metaData = conn.getMetaData();//获取DatabaseMetaData
                String[] types = {"TABLE"};
                ResultSet tabs = metaData.getTables(null, null, tableName, types);
                if (tabs.next()) {//查询
                    flag = true;
                }
                tabs.close();
            } catch (SQLException e) {
                logger.error("queryString fails:\t" + e.getMessage());
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 添加数据
     *
     * @param tableName
     * @param columns
     * @param data
     */
    public static void insert (String baseName, String tableName, String[] columns, List<String[]> data) {
        Connection conn = getConnection(baseName);
        if (!isConnection(conn)) {
            logger.debug("reconnecting...");
            return;
        } else {
            //获取模版sql
            StringBuffer sql = null;
            String[] oneData = data.get(0);
            //insert into tablename (columns) values (value1,value2,...);
            sql = new StringBuffer("INSERT IGNORE INTO " + tableName + " (");
            for (int j = 0; j < columns.length; j++) {
                sql.append(columns[j]);
                if (j < columns.length - 1) {//防止最后一个
                    sql.append(",");
                }
            }
            sql.append(")").append(" VALUES (");
            for (int k = 0; k < oneData.length; k++) {
                sql.append("?");
                if (k < oneData.length - 1) {
                    sql.append(",");
                }
            }
            sql.append(");");
            //执行(批量)
            try {
                //预处理SQL,防止注入
                PreparedStatement preparedStatement = conn.prepareStatement(sql.toString());
                for (int i = 0; i < data.size(); i++) {
                    String[] tmpData = data.get(i);
                    for (int j = 0; j < tmpData.length; j++) {
                        preparedStatement.setString(j + 1, tmpData[j]);//设置参数
                    }
                    preparedStatement.addBatch();//加入批量处理
                }
                preparedStatement.executeBatch();//执行批量处理
                preparedStatement.close();
                conn.commit();//提交
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();//数据回滚
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
                e.printStackTrace();
                logger.error("add data field:\t" + e.getMessage());
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 查询表中特有列的值
     *
     * @param tableName
     * @param targetColumn
     * @param offerColumn
     * @param data
     * @return
     */
    public static String queryString (String baseName, String tableName, String targetColumn, String offerColumn, String data) {
        Connection conn = getConnection(baseName);
        //select targetColumn from tablename where offerColumn = 'data';Z
        String sql = "SELECT " + targetColumn + " FROM " + tableName +
                " WHERE " + offerColumn + "='" + data + "';";
        ResultSet resultSet = null;
        try {
            //预处理
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            if (preparedStatement.execute(sql)) {
                resultSet = preparedStatement.executeQuery(sql);
                if (resultSet.next()) {
                    return resultSet.getString(resultSet.getRow());
                }
            }
            preparedStatement.close();
        } catch (SQLException e) {
            logger.error("syntax sql");
        } finally {
            try {
                resultSet.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 查询表大小
     *
     * @param tableName
     * @param targetColumn
     * @return
     */
    public static int queryTableSize (String baseName, String tableName, String targetColumn) {
        Connection conn = getConnection(baseName);
        if (!isConnection(conn)) {
            logger.error("connect failed");
            return 0;
        }
        String sql = "select count(" + targetColumn + ") from " + tableName;
        int count = 0;
        ResultSet set = null;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            if (preparedStatement.execute(sql)) {//检验语句是否正确
                set = preparedStatement.executeQuery(sql);
                if (set.next()) {//游标调整
                    count = Integer.parseInt(set.getString(1));
                }
                set.close();
            }
            preparedStatement.close();
        } catch (SQLException e) {
            logger.error("syntax error");
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * 获取单个列内容
     *
     * @param tableName
     * @param targetColumn
     * @return
     */
    public static List<String> getList (String baseName, String tableName, String targetColumn) {
        List<String> titleName = new ArrayList<>();
        Connection conn = getConnection(baseName);
        if (!isConnection(conn)) {
            logger.error("connect failed");
            return titleName;
        }
        String sql = "select " + targetColumn + " from " + tableName + ";";
        ResultSet set = null;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            if (preparedStatement.execute(sql)) {
                set = preparedStatement.executeQuery(sql);
                while (set.next()) {//调整游标位置
                    titleName.add(set.getString(1));//当前游标位置1
                }
                set.close();
            }
            preparedStatement.close();
        } catch (SQLException e) {
            logger.error("syntax error:" + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return titleName;
    }

    /**
     * 获取两对列内容
     *
     * @param baseName
     * @param tableName
     * @param targetColumn1
     * @param targetColumn2
     * @return
     */
    public static Map<String, String> getUrlList (String baseName, String tableName, String targetColumn1, String targetColumn2) {
        Map<String, String> urlList = new HashMap<>();
        Connection conn = getConnection(baseName);
        if (!isConnection(conn)) {
            logger.error("connect failed");
            return urlList;
        }
        String sql = "select " + targetColumn1 + "," + targetColumn2 + " from " + tableName + ";";
        ResultSet set;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            if (preparedStatement.execute(sql)) {
                set = preparedStatement.executeQuery(sql);
                while (set.next()) {
                    urlList.put(set.getString(1), set.getString(2));
                }
                set.close();
            }
            preparedStatement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return urlList;
    }

    /**
     * 获取表结构信息
     *
     * @param baseName
     * @param tableName
     * @return
     */
    public static Date[] getTableStatus (String baseName, String tableName) {
        String sql = "show table status from " + baseName + " like '" + tableName + "';";
        Connection conn = getConnection(baseName);
        if (!isConnection(conn)) {
            logger.error("connect failed");
            return null;
        }
        Date[] dateStatus = new Date[2];
        ResultSet set = null;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            if (preparedStatement.execute(sql)) {
                set = preparedStatement.executeQuery(sql);
                if (set.next()) {
                    dateStatus[0] = set.getDate("Create_time");
                    dateStatus[1] = set.getDate("Update_time");
                }
                set.close();
            }
            preparedStatement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dateStatus;
    }


}
