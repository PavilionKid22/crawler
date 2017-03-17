package cn.mvncode.webcrawler.downloadpage;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Request;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Pavilion on 2017/3/16.
 */
public class downloadpageTest {
    @Test
    public void download () throws Exception {
        new downloadpage().download(new Request("http://www.cnblogs.com/yaowen/p/3757571.html"),
                CrawlerSet.set().setRetryTimes(3).setTimeOut(5000));
//        new downloadpage().download(new Request("http://blog.csdn.net/ktlifeng/article/details/50858348"),
//                CrawlerSet.set().setRetryTimes(3).setTimeOut(5000));
    }

}