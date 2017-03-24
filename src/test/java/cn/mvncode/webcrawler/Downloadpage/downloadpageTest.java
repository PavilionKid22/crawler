package cn.mvncode.webcrawler.Downloadpage;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Request;
import org.junit.Test;

import java.net.InetAddress;

/**
 * Created by Pavilion on 2017/3/16.
 */
public class downloadpageTest {
    @Test
    public void download () throws Exception {
//        new DownloadPage().download(new Request("http://www.cnblogs.com/yaowen/p/3757571.html"),
//                CrawlerSet.set().setRetryTimes(3).setTimeOut(5000));
        new DownloadPage().download(new Request("http://www.xdaili.cn/freeproxy.html"),
                CrawlerSet.setByDefault(), null);
//        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }

}