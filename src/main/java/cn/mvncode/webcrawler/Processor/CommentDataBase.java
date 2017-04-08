package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.Utils.DataBaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Pavilion on 2017/3/21.
 */
public class CommentDataBase implements Runnable {

    private Logger logger = LoggerFactory.getLogger(CommentDataBase.class.getName());
    private boolean isRunning = true;

    private String tableName;
    private ResultItem result;

    private Connection conn;

    private String sql = "CREATE TABLE " + tableName + " (" +
            "User VARCHAR(128) NOT NULL PRIMARY KEY," +
            "UserID CHAR(10) NOT NULL," +
            "Vote CHAR(8) NOT NULL," +
            "Star CHAR(4) NOT NULL," +
            "Date CHAR(40) NOT NULL," +
            "Comment MEDIUMTEXT" +
            ")DEFAULT CHARSET=utf8;";


    public CommentDataBase (String tableName, ResultItem result) {
        this.tableName = tableName;
        this.result = result;
    }


    /**
     * 添加数据
     *
     * @throws SQLException
     */
    public void insertData () throws SQLException {

        conn = DataBaseUtil.getConnection();
        conn.setAutoCommit(false);//设置手动提交
        if (!DataBaseUtil.isConnection(conn)) {
            logger.debug("reconnect...");
            //do something
        } else {
            if (!DataBaseUtil.exitTable(tableName, conn)) {
                DataBaseUtil.createTable(conn, sql);
            }
            List<String[]> data = new ArrayList<>();//所有数据
            for (Map.Entry<String, String> entry : result.getComment().entrySet()) {
                String[] oneData = getData(entry.getKey(), entry.getValue());
                if (oneData.length != 6) continue;
                data.add(oneData);
            }
            //insert into tableName (User,UserID,Vote,Star,Date,Comment) values (.., .., ..);
            String[] columns = new String[6];
            columns[0] = "User";
            columns[1] = "UserID";
            columns[2] = "Vote";
            columns[3] = "Star";
            columns[4] = "Data";
            columns[5] = "Comment";
            DataBaseUtil.insert(conn, tableName, columns, data);
            isRunning = false;
        }

    }

    /**
     * 拆分数据
     *
     * @return
     */
    public String[] getData (String key, String value) {
        List<String> tmpData = new ArrayList<String>();
        //key: User\tUserID
        //value:Comment\tVote\tTime\tStar
        String[] tmpKeyData = key.split("\t");
        for (int i = 0; i < tmpKeyData.length; i++) {
            tmpData.add(tmpKeyData[i]);
        }
        String[] tmpValueData = value.split("\t");
        for (int i = 0; i < tmpValueData.length; i++) {
            tmpData.add(tmpValueData[i]);
        }
        String[] data = new String[tmpData.size()];
        for (int i = 0; i < tmpData.size(); i++) {
            data[i] = tmpData.get(i);
        }
        return data;
    }


    @Override
    public void run () {

        while (isRunning) {
            try {
                insertData();
            } catch (SQLException e) {
//                e.printStackTrace();
                logger.error("insert data failed");
            }
        }

    }
}
