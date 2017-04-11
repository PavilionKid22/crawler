package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.Processor.CommentDataBase;
import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.Utils.DataBaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 将抓取内容写入数据库
 * Created by Pavilion on 2017/4/10.
 */
public class CommentPushDatabase implements Runnable {

    private Logger logger = LoggerFactory.getLogger(CommentPushDatabase.class.getName());

    private ExecutorService updateDataBaseService = Executors.newFixedThreadPool(10);

    private boolean isRunning = true;
    private int count = 0;

    public static Map<String, ResultItem> list = new HashMap<String, ResultItem>();
    public static boolean updateCommentFlag = false;
    public static String tableName = null;

    @Override
    public void run () {

        int totalThreads = DataBaseUtil.queryTableSize("moviebase","movies", "UID");
        while (isRunning) {
            if(updateDataBaseService.isTerminated()){
                isRunning = false;
            }
            if (updateCommentFlag) {//注册一个线程
                String table = "tb_"+tableName;
                if (DataBaseUtil.exitTable("moviebase",table)) {//判断表是否存在
                    int beforeUpdateCount = DataBaseUtil.queryTableSize("moviebase",table, "UserID");
                    int afterUpdateCount = list.get(tableName).getComment().size();
                    if (afterUpdateCount - beforeUpdateCount >= 50) {//更新评论数量应多于50
                        logger.info(tableName + ": update comment to database");
                        CommentDataBase commentDataBase = new CommentDataBase(table, list.get(tableName));
                        updateDataBaseService.execute(commentDataBase);
                    }
                } else {
                    logger.info(tableName + ": update comment to database");
                    CommentDataBase commentDataBase = new CommentDataBase(table, list.get(tableName));
                    updateDataBaseService.execute(commentDataBase);
                }
                count++;
                updateCommentFlag = false;
            }
            if (count == totalThreads) {
                updateDataBaseService.shutdown();
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
