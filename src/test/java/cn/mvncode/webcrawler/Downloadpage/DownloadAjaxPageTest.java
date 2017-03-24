package cn.mvncode.webcrawler.Downloadpage;

import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.Request;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Pavilion on 2017/3/24.
 */
public class DownloadAjaxPageTest {
    @Test
    public void getPage () throws Exception {
        new DownloadAjaxPage().getPage(new Request("http://www.xdaili.cn/freeproxy.html"));
    }

}