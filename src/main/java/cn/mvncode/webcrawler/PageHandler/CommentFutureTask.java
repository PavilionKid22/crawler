package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.Processor.Console;
import cn.mvncode.webcrawler.ResultItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 包装PageCommentHandler
 * 获取comment线程返回值
 * 输出到CommentPushDataBase
 * Created by Pavilion on 2017/4/6.
 */
public class CommentFutureTask extends FutureTask<ResultItem> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ResultItem commentResult = new ResultItem();

    private static int errorCount = 0;

    public CommentFutureTask (Callable<ResultItem> callable) {
        super(callable);
    }

    @Override
    protected void done () {
        try {
            commentResult = get();
            if (commentResult.getComment().size() < 100) {
                logger.error("network error: " + ++errorCount);
                if (errorCount > 5) System.exit(-2);//网络问题终止程序
                return;
            }
        } catch (InterruptedException e) {
            logger.error("PageCommentHandler execute failed: " + e.getMessage());
            ++errorCount;
        } catch (ExecutionException e) {
            logger.error("PageCommentHandler execute failed: " + e.getMessage());
            ++errorCount;
        }
        //同步到数据库推送线程
        synchronized (this) {
            CommentPushDatabase.list.put(commentResult.getTitle(), commentResult);
            CommentPushDatabase.updateCommentFlag = true;
            CommentPushDatabase.tableName = commentResult.getTitle();
            Console.totalComments += commentResult.getComment().size();
            Console.tableCount++;
        }
    }


}
