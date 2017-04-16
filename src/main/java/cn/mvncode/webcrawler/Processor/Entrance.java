package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

/**
 * 主函数入口
 * Created by Pavilion on 2017/3/26.
 */
public class Entrance {

    private static final Logger logger = LoggerFactory.getLogger(Entrance.class);

    public static void main (String[] args) throws ParseException {

        logger.info("Start:");

        String cookie = getCookie();

        CrawlerSet set = CrawlerSet.setDefault().setLaunchProxyPool(false).addCookie("Cookie",cookie);
        Request request = new Request("https://movie.douban.com/explore#!type=movie&tag=%E8%B1%86%E7%93%A3%E9%AB%98%E5%88%86&sort=recommend&page_limit=20&page_start=0");
        new Console(set, request, null).process();

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
