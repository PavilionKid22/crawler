package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.Utils.DataBaseUtil;
import cn.mvncode.webcrawler.Utils.MyStringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pavilion on 2017/3/21.
 */
public class CommentDataBase implements Runnable {

    private Logger logger = LoggerFactory.getLogger(CommentDataBase.class.getName());
    private boolean isRunning = true;

    private String tableName;
    private ResultItem result;


    public CommentDataBase (String tableName, ResultItem result) {
        this.tableName = "tb_" + tableName;
        this.result = result;
    }


    /**
     * 添加数据
     *
     * @throws SQLException
     */
    public void insertData () throws SQLException {

        if (!DataBaseUtil.exitTable("moviebase", tableName)) {
            String sql = "CREATE TABLE " + tableName + " (" +
                    "UserID char(10) NOT NULL PRIMARY KEY," +
                    "User varchar(128) NOT NULL," +
                    "Vote char(8) NOT NULL," +
                    "Star char(8) NOT NULL," +
                    "Date char(40) NOT NULL," +
                    "Comment mediumtext" +
                    ")DEFAULT CHARSET=utf8;";
            DataBaseUtil.createTable("moviebase", sql);
            logger.info("create table " + tableName);
        } else {
            List<String[]> data = new ArrayList<>();//所有数据
            for (Map.Entry<String, String> entry : result.getComment().entrySet()) {
                String[] oneData = getData(entry.getKey(), entry.getValue());
                data.add(oneData);
            }
            //insert into tableName (User,UserID,Vote,Star,Date,Comment) values (.., .., ..);
            String[] columns = new String[6];
            columns[0] = "UserID";
            columns[1] = "User";
            columns[2] = "Vote";
            columns[3] = "Star";
            columns[4] = "Date";
            columns[5] = "Comment";
            DataBaseUtil.insert("moviebase", tableName, columns, data);
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
                logger.error("insert data failed");
            }
        }
        logger.info("insert comment over");

    }

}
