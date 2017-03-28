package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.Utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 主函数入口
 * Created by Pavilion on 2017/3/26.
 */
public class Entrance {

    private static final Logger logger = LoggerFactory.getLogger(Entrance.class);
    private static DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd hh:mm:ss");

    private static final String path = "D:\\IdeaPro\\crawler\\src\\main\\resources\\cookie.txt";


    public static void main (String[] args) throws ParseException {

        logger.info("Start:");
        Date start = dateFormat.parse(DateUtil.timeNow());

        String cookie = null;
        try {
            byte[] content = Files.readAllBytes(Paths.get(path));
            cookie = new String(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(cookie);

        CrawlerSet set = CrawlerSet.setDefault().addCookie("cookie", cookie).setLaunchProxyPool(false);
        Request request = new Request("https://movie.douban.com/subject/26648249/comments?status=P");

        new Console(set, request, null).process();


        Date end = dateFormat.parse(DateUtil.timeNow());
        long time = (end.getTime() - start.getTime()) / (1000 * 60);//minute
        logger.info("cost:" + String.valueOf(time) + "minutes");
        logger.info("End");
    }

}
