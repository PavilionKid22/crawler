package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.Downloadpage.Downloader;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.ResultItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.StatusLine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 网页解析
 * Created by Pavilion on 2017/3/16.
 */
public class PageCommentHandler extends PageResponseHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 获取网页处理结果
     *
     * @return result
     */
    @Override
    public ResultItem getHandler (Request seek, CrawlerSet set, Proxy proxy, Downloader downloader) throws IOException {
        this.proxy = proxy;
        handleResponse(seek, set, downloader);
        return resultItem;
    }

    /**
     * 处理网页（0.1.2）
     *
     * @param seek
     * @param set
     * @param downloader
     * @return
     * @throws IOException
     */
    @Override
    public void handleResponse (Request seek, CrawlerSet set, Downloader downloader) throws IOException {

        Page page = downloader.download(seek, set, proxy);
        String refer = page.getUrl();

        //解析网页
        while (!page.getTargetUrls().isEmpty()) {

            logger.info("Url = " + page.getUrl());
            if (proxy != null) {
                logger.info("proxy = " + proxy.getHttpHost().getHostName());
            }

            StatusLine statusLine = page.getHttpResponse().getStatusLine();
            if (statusLine.getStatusCode() >= 300) {
                break;
            }
            //格式化html
            Document document = Jsoup.parse(page.getPlainText());
            //获取内容
            Map<String, String> comments = getComments(document);
            for (Map.Entry<String, String> entry : comments.entrySet()) {
                resultItem.addComment(entry.getKey(), entry.getValue());
            }
            //获取下一页url
            Request newRequest = getUrl(document, refer);
            //获取refer
            newRequest.setRefer(page.getUrl());
            //删除当前处理url
            page.removeTargetUrl(page.getRequest());
            //抓取间隔
            try {
                TimeUnit.MILLISECONDS.sleep(new Random().nextInt(3000 - 1000 + 1) + 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //加载新页面
            page = downloader.download(newRequest, set, proxy);

        }

    }


    /**
     * 获取指定url(豆瓣定制)
     *
     * @param document
     * @return
     */
    private Request getUrl (Document document, String refer) {

        Request url = new Request();

        Elements links = document.select("div#paginator");
        for (Element element : links) {
            Elements link = element.select("a[href].next");
            int i = StringUtils.indexOf(refer, "?status=P", 1);
            refer = StringUtils.substring(refer, 0, i);
            String newUrl = refer + "/" + link.attr("href");
            url.setUrl(newUrl);
        }

        return url;
    }


    /**
     * 获取内容(豆瓣定制)
     * id+avatar+comment+votes+time
     *
     * @param document
     * @return
     */
    private Map<String, String> getComments (Document document) {

        Map<String, String> comments = new LinkedHashMap<String, String>();

        Elements elements = document.select("div.comment-item");
        for (Element element : elements) {
            StringBuffer comment = new StringBuffer();
            StringBuffer avator = new StringBuffer();
            //获取用户名和id
            Elements name = element.select("a[title]");
            avator.append(name.attr("title")).append("(")
                    .append(element.attr("data-cid")).append(")");

            //获取评论内容，点赞数和日期
            Elements sentence = element.select("p");
            Elements votes = element.select("span.votes");
            Elements time = element.select("span.comment-time");
            comment.append(sentence.text()).append("\t(votes:").append(votes.text())
                    .append("\ttime:").append(time.attr("title")).append(")");

            comments.put(avator.toString(), comment.toString());
        }

        return comments;
    }
}
