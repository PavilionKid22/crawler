package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.Utils.DateUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 主函数入口
 * Created by Pavilion on 2017/3/26.
 */
public class Entrance {

    public static void main (String[] args) {

        System.out.println("Start:" + DateUtil.timeNow());

        String cookie = null;
        try {
            byte[] content = Files.readAllBytes(Paths.get("D:\\IdeaPro\\crawler\\src" +
                    "\\main\\java\\cn\\mvncode\\webcrawler\\Processor\\cookie.txt"));
            cookie = new String(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(cookie);

        CrawlerSet set = CrawlerSet.setDefault().addCookie("cookie", cookie).setLaunchProxyPool(false);
        Request request = new Request("https://movie.douban.com/subject/26648249/comments?status=P");

        new Console(set, request, null).process();
    }

}
