package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.Downloadpage.Downloader;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Pavilion on 2017/4/12.
 */
public class CommentSubmitThreadTest {
    @Test
    public void getUrlList () throws Exception {

        CrawlerSet set = CrawlerSet.setDefault();
        Downloader downloader = new DownloadPage();
        Map<String, String> urlList = new CommentSubmitThread(set, null, downloader).getUrlList();
        for (Map.Entry<String, String> entry : urlList.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

    }

}