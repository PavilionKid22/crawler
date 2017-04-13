package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.Processor.CommentDataBase;
import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.Utils.DataBaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 将抓取内容写入数据库
 * Created by Pavilion on 2017/4/10.
 */
public class CommentPushDatabase implements Runnable {

    private Logger logger = LoggerFactory.getLogger(CommentPushDatabase.class.getName());

    private ExecutorService updateDataBaseService = Executors.newFixedThreadPool(10);

    private boolean isRunning = true;

    private String baseName = "moviebase";

    public static Map<String, ResultItem> list = new HashMap<String, ResultItem>();
    public static boolean updateCommentFlag = false;
    public static String tableName = null;
    public static int submitThreadCount = 0;//提交抓取评论的线程数量(CommentSubmitThread)

    @Override
    public void run () {

        int count = 0;
        while (isRunning) {
            if (updateDataBaseService.isTerminated()) {
                isRunning = false;
            }
            if (updateCommentFlag) {//注册一个线程
                String databaseTable = "tb_" + tableName;//创建的数据库表以"tb_"开头
                if (DataBaseUtil.exitTable(baseName, databaseTable)) {//判断表是否存在
                    //距离上次更新相距一周
                    Date[] dates = DataBaseUtil.getTableStatus(baseName, databaseTable);
                    long nowTime = new Date().getTime();
                    boolean isTime = false;
                    if (dates[1] != null) {
                        long updateTime = dates[1].getTime();
                        if (((nowTime - updateTime) / (1000 * 60 * 60 * 24)) >= 7) isTime = true;
                    } else {
                        long createTime = dates[0].getTime();
                        if (((nowTime - createTime) / (1000 * 60 * 60 * 24)) >= 7) isTime = true;
                    }
                    if(isTime){
                        int beforeUpdateCount =
                                DataBaseUtil.queryTableSize(baseName, databaseTable, "UserID");
                        int afterUpdateCount = list.get(tableName).getComment().size();
                        //更新评论数量应多于50
                        if (afterUpdateCount - beforeUpdateCount >= 50) {
                            logger.info(tableName + ": update comment to database");
                            CommentDataBase commentDataBase = new CommentDataBase(tableName, list.get(tableName));
                            updateDataBaseService.execute(commentDataBase);
                        }
                    }
                } else {
                    if (list.get(tableName).getComment().size() != 0) {
                        logger.info(tableName + ": update comment to database");
                        CommentDataBase commentDataBase = new CommentDataBase(tableName, list.get(tableName));
                        updateDataBaseService.execute(commentDataBase);
                    }
                }
                count++;
                updateCommentFlag = false;
                if (count == submitThreadCount) {
                    logger.info("terminated submit pushThread");
                    updateDataBaseService.shutdown();
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
