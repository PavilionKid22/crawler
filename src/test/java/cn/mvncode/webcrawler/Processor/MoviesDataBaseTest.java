package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.Downloadpage.Downloader;
import cn.mvncode.webcrawler.PageHandler.PageListHandler;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.ResultItem;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Created by Pavilion on 2017/4/9.
 */
public class MoviesDataBaseTest {
    @Test
    public void insertData () throws Exception {


        PageListHandler pageListHandler = new PageListHandler();
        Request request = new Request("https://movie.douban.com/explore#!type=movie&tag=%E7%83%AD%E9%97%A8&sort=time&page_limit=20&page_start=0");
        CrawlerSet set = CrawlerSet.setDefault().addCookie("Cookie", getCookie());
        Proxy proxy = null;
        Downloader downloader = new DownloadPage();
        ResultItem result = null;

        System.out.println("getting url list...");//ttttttttttt
        result = pageListHandler.getHandler(request, set, proxy, downloader);
        System.out.println("size is " + result.getFields().size());//ttttttt
//        new Thread(new MoviesDataBase(result)).start();
        new MoviesDataBase(result).run();

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