package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.Downloadpage.Downloader;
import cn.mvncode.webcrawler.PageHandler.PageCommentHandler;
import cn.mvncode.webcrawler.PageHandler.PageListHandler;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.Utils.MyStringUtil;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Observer;

import static org.junit.Assert.*;

/**
 * Created by Pavilion on 2017/4/9.
 */
public class CommentDataBaseTest {
    @Test
    public void insertData () throws Exception {

        Request request = new Request("https://movie.douban.com/subject/10752547/");
        String title = "tb_夜色人生";
        CrawlerSet set = CrawlerSet.setDefault().addCookie("Cookie", getCookie());
        Proxy proxy = null;
        Downloader downloader = new DownloadPage();

        PageCommentHandler pageCommentHandler = new PageCommentHandler(request, set, proxy, downloader, title);
        ResultItem result = pageCommentHandler.call();

        CommentDataBase commentDataBase = new CommentDataBase(title,result);
        commentDataBase.run();


    }

    public static String getCookie () {
        String path = "D:\\IdeaPro\\crawler\\src\\main\\resources\\cookie.txt";
        String cookie = null;
        try {
            byte[] content = Files.readAllBytes(Paths.get(path));
            cookie = new String(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cookie;
    }


}