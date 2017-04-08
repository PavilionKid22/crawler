package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.Utils.DataBaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Pavilion on 2017/4/8.
 */
public class MoviesDataBase implements Runnable{

    private Logger logger = LoggerFactory.getLogger(MoviesDataBase.class.getName());

    private Connection conn;
    private boolean isRunning = true;

    private String sql;
    private String tableName = "movies";
    private ResultItem fields;

    public MoviesDataBase (ResultItem fields) {
        this.fields = fields;
    }

    /**
     * 添加数据
     ** @throws SQLException
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
            for (Map.Entry<String, Object> entry : fields.getFields().entrySet()) {
                String[] oneData = new String[2];
                oneData[0] = entry.getKey();
                oneData[1] = (String) entry.getValue();
                if (oneData.length != 2) continue;
                data.add(oneData);
            }
            //insert into tableName (User,UserID,Vote,Star,Date,Comment) values (.., .., ..);
            String[] columns = new String[2];
            columns[0] = "Title";
            columns[1] = "Url";
            DataBaseUtil.insert(conn, tableName, columns, data);
            isRunning = false;
        }

    }

    @Override
    public void run () {

        while (isRunning){
            try {
                insertData();
            } catch (SQLException e) {
//                e.printStackTrace();
                logger.error("insert fields failed");
            }
        }

    }
}
