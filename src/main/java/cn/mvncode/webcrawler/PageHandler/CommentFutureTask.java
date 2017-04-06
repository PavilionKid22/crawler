package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.Processor.Console;
import cn.mvncode.webcrawler.ResultItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by Pavilion on 2017/4/6.
 */
public class CommentFutureTask extends FutureTask<ResultItem> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String name;
    private ResultItem commentResult;

    public CommentFutureTask (Callable<ResultItem> callable, String name) {
        super(callable);
        this.name = name;
        commentResult = new ResultItem();
    }

    @Override
    protected void done () {
//        super.done();
        try {
            commentResult = get();
            logger.info(name + " comment fetch over, size is " + Integer.toString(commentResult.getComment().size()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (commentResult != null) {
            Console.list.put(name, commentResult);
            System.out.println(Console.list.size());//tttttttttttttttttttttt
        }
    }


}
