package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.Downloader;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.Utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.StatusLine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 网页解析
 * Created by Pavilion on 2017/3/16.
 */
public class PageCommentHandler extends PageResponseHandler implements Callable<ResultItem> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ResultItem resultItem;

    private Page page;
    private String refer;

    private Request seek;
    private CrawlerSet set;
    private Proxy proxy;
    private Downloader downloader;
    private String name;

    public PageCommentHandler (Request seek, CrawlerSet set, Proxy proxy, Downloader downloader, String name) {
        this.seek = seek;
        this.set = set;
        this.proxy = proxy;
        this.downloader = downloader;
        this.name = name;
        resultItem = new ResultItem();
        set.setDomain(seek.getUrl());
    }

    /**
     * 获取网页处理结果
     *
     * @return result
     */
    @Override
    public ResultItem getHandler (Request seek, CrawlerSet set, Proxy proxy, Downloader downloader) throws IOException {
        return null;
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

        String tmpSeek = seek.getUrl();
        Pattern pattern = Pattern.compile("(https://movie.douban.com/subject/)[0-9/]{8,}");
        Matcher matcher = pattern.matcher(tmpSeek);
        if (matcher.find()) {
            tmpSeek += "comments?status=P";
            seek.setUrl(tmpSeek);
            page = downloader.download(seek, set, proxy);
        } else {//退出线程
            try {
                throw new Exception("error url");
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        Elements link = links.select("a[href].next");
        int i = StringUtils.indexOf(refer, "?status=P", 1);
        refer = StringUtils.substring(refer, 0, i);
        String newUrl = refer + "/" + link.attr("href");
        url.setUrl(newUrl.trim());

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


    @Override
    public ResultItem call () throws Exception {
        //初始化page
        handleResponse(seek, set, downloader);
        //初始化refer
        refer = page.getUrl();
//        System.out.println("Refer is " + refer);

        //解析网页
        while (!page.getTargetUrls().isEmpty()) {
            logger.info("Url = " + page.getUrl() + "\t" + name);
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
            if (newRequest.getUrl().equals(refer.substring(0, StringUtils.indexOf(refer, "?status=P")) + "/")) {
                break;
            }
            //设置refer
            newRequest.setRefer(page.getUrl());
            //删除当前处理url
            page.removeTargetUrl(page.getRequest());
            //抓取间隔
            try {
                TimeUnit.MILLISECONDS.sleep(new Random().nextInt(5000 - 1000 + 1) + 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //加载新页面
            page = downloader.download(newRequest, set, proxy);
        }

        return resultItem;
    }
}
