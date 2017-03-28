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
        new DownloadAjaxPage().download(new Request("http://www.xdaili.cn/freeproxy.html"), CrawlerSet.setDefault(), null);
    }

}