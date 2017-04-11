package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.Utils.DataBaseUtil;
import cn.mvncode.webcrawler.Utils.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Pavilion on 2017/4/8.
 */
public class MoviesDataBase implements Runnable {

    private Logger logger = LoggerFactory.getLogger(MoviesDataBase.class.getName());

    private boolean isRunning = true;
    private int beforeUpdate = 0;
    private int afterUpdate = 0;

    private String tableName = "movies";
    private ResultItem fields;

    public MoviesDataBase (ResultItem fields) {
        this.fields = fields;
    }

    /**
     * 添加数据
     * * @throws SQLException
     */
    public void insertData () throws SQLException {
        if (!DataBaseUtil.exitTable("moviebase", tableName)) {
            String sql = "CREATE TABLE " + tableName + "(" +
                    "UID char(10) NOT NULL primary key," +
                    "Title varchar(128) NOT NULL," +
                    "Url varchar(128) NOT NULL" +
                    ")DEFAULT CHARSET=utf8;";
            DataBaseUtil.createTable("moviebase", sql);
        } else {
            beforeUpdate = DataBaseUtil.queryTableSize("moviebase",tableName, "UID");
            List<String[]> data = new ArrayList<>();//所有数据
            for (Map.Entry<String, Object> entry : fields.getFields().entrySet()) {
                String[] oneData = new String[3];
                oneData[0] = UrlUtils.getUID((String) entry.getValue());
                oneData[1] = entry.getKey();
                oneData[2] = (String) entry.getValue();
                data.add(oneData);
            }
            //insert into tableName (......) values (.., .., ..);
            String[] columns = new String[3];
            columns[0] = "UID";
            columns[1] = "Title";
            columns[2] = "Url";
            DataBaseUtil.insert("moviebase",tableName, columns, data);
            afterUpdate = DataBaseUtil.queryTableSize("moviebase",tableName, "UID");
            isRunning = false;
        }
    }

    @Override
    public void run () {

        while (isRunning) {
            try {
                insertData();
            } catch (SQLException e) {
                logger.error("insert fields failed");
            }
        }
        logger.info("update the movies over, update size is " + (afterUpdate - beforeUpdate));

    }

}
