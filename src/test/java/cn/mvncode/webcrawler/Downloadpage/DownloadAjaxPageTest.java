package cn.mvncode.webcrawler.Downloadpage;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Request;
import org.junit.Test;

/**
 * Created by Pavilion on 2017/3/24.
 */
public class DownloadAjaxPageTest {
    @Test
    public void getPage () throws Exception {
        new DownloadAjaxPage().download(new Request("https://movie.douban.com/explore#!type=movie&tag=%E7%83%AD%E9%97%A8&sort=recommend&page_limit=20&page_start=0"),
                CrawlerSet.setDefault(),
                null);
    }

}