package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.ResultItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 获取comment线程返回值
 * 输出到CommentPushDataBase
 * Created by Pavilion on 2017/4/6.
 */
public class CommentFutureTask extends FutureTask<ResultItem> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ResultItem commentResult;

    public CommentFutureTask (Callable<ResultItem> callable) {
        super(callable);
        commentResult = new ResultItem();
    }

    @Override
    protected void done () {
        try {
            commentResult = get();
            logger.info(commentResult.getTitle() + " comment fetch over, size is "
                    + Integer.toString(commentResult.getComment().size()));
        } catch (InterruptedException e) {
            logger.error("PageCommentHandler execute failed");
        } catch (ExecutionException e) {
            logger.error("PageCommentHandler execute failed");
        }
        if (commentResult != null) {//同步到数据库推送线程
            synchronized (this) {
                CommentPushDatabase.list.put(commentResult.getTitle(), commentResult);
                CommentPushDatabase.updateCommentFlag = true;
                CommentPushDatabase.tableName = commentResult.getTitle();
            }
        }
    }


}
