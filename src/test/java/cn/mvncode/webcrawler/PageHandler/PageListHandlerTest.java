package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.ResultItem;
import org.junit.Test;

import java.util.Map;

/**
 * Created by Pavilion on 2017/3/28.
 */
public class PageListHandlerTest {

//    @Test
//    public void getTag () throws Exception {
//        PageListHandler pageListHandler = new PageListHandler();
//        String tmp = pageListHandler.getTag("https://movie.douban.com/explore#!type=movie&tag=%E7%83%AD%E9%97%A8&sort=recommend&page_limit=20&page_start=0");
//        System.out.println(tmp);
//    }

    @Test
    public void getHandler () throws Exception {
        ResultItem result = new ResultItem();
        PageListHandler pageListHandler = new PageListHandler();

        Request request = new Request("https://movie.douban.com/explore#!type=movie&tag=%E7%83%AD%E9%97%A8&sort=time&page_limit=20&page_start=0");
        CrawlerSet set = CrawlerSet.setDefault().addCookie("cookie", "ll=\"118172\"; bid=ubEjM5_AvE0; ps=y; ue=\"15957119500@163.com\"; dbcl2=\"159332631:evWlNwMx2aM\"; ct=y; ck=rER4; _vwo_uuid_v2=463A2B5EA46184CF16D87860722C39A5|93d29d8a851a42fed9a454655491e5d3; ap=1; push_noty_num=0; push_doumail_num=0; _pk_ref.100001.8cb4=%5B%22%22%2C%22%22%2C1490680329%2C%22https%3A%2F%2Fmovie.douban.com%2Fsubject%2F26648249%2Fcomments%3Fsort%3Dnew_score%26status%3DP%22%5D; _pk_id.100001.8cb4=a56d68cf7bd21ddb.1490017477.10.1490680329.1490673518.; _pk_ses.100001.8cb4=*; __utmt=1; __utma=30149280.1053201544.1490011191.1490672520.1490680248.15; __utmb=30149280.2.10.1490680248; __utmc=30149280; __utmz=30149280.1490011191.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utmv=30149280.15933");
        DownloadPage downloader = new DownloadPage();

        result = pageListHandler.getHandler(request, set, null, downloader);

        for (Map.Entry<String, Object> entry : result.getFields().entrySet()) {
            System.out.println("Title = " + entry.getKey() + " Url = " + entry.getValue());
        }
    }

}