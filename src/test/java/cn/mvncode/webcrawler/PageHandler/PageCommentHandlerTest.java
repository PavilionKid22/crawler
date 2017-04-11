package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.Downloadpage.Downloader;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavilion on 2017/4/9.
 */
public class PageCommentHandlerTest {
    @Test
    public void getHandler () throws Exception {

        CrawlerSet set = CrawlerSet.setDefault();
        Proxy proxy = null;
//        Request request = new Request("https://www.douban.com/misc/sorry?original-url=https%3A%2F%2Fmovie.douban.com%2Fsubject%2F26329796%2Fcomments%3Fstatus%3DP");
        Request request = new Request("https://movie.douban.com/subject/26259641/comments?status=P");
        Downloader downloader = new DownloadPage();

        Page page = downloader.download(request, set, proxy);

//        System.out.println(page.getStatusCode());
////        System.out.println(page.getPlainText());
        String content = page.getPlainText();
        Document document = Jsoup.parse(content);
//        Elements h1 = document.select("h1");
//        Elements h2 = document.select("h2");
//        System.out.println(h1.text());
//        System.out.println(h2.text());
        Elements elements = document.select("div.comment-item");
        for (Element ele : elements) {
//            Elements tmp = ele.select("span.comment-info");
//            System.out.println(tmp.toString());
            Elements a = ele.select("span[title]");
            String[] b = a.toString().split("\n");
            System.out.println(b[0]);
            if (b.length > 1) {
                String c = "allstar";
                int index = StringUtils.indexOf(b[0], c) + c.length();
                String d = b[0].substring(index, index + 2);
                System.out.println(d);
            } else {
                System.out.println("null");
            }
//            System.out.println(a.toString());

        }
    }

}