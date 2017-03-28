package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.Proxy.GetProxyThread;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.Utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.StatusLine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 网页解析
 * Created by Pavilion on 2017/3/16.
 */
public class PageCommentHandler extends PageResponseHandler{

//    private ResultItem resultItem;
//    private Proxy proxy;

    public PageCommentHandler () {
        resultItem = new ResultItem();
    }


    /**
     * 获取网页处理结果
     *
     * @return result
     */
    @Override
    public ResultItem getHandler (Request seek, CrawlerSet set, Proxy proxy, DownloadPage downloadPage) throws IOException {
        this.proxy = proxy;
        handleResponse(seek, set, downloadPage);
        return resultItem;
    }

    /**
     * 处理网页（0.1.2）
     *
     * @param seek
     * @param set
     * @param downloador
     * @return
     * @throws IOException
     */
    @Override
    public void handleResponse (Request seek, CrawlerSet set, DownloadPage downloador) throws IOException {

        Page page = downloador.download(seek, set, proxy);
        String refer = page.getUrl();

        //解析网页
        while (!page.getTargetUrls().isEmpty()) {

            System.out.print("Download url = " + page.getUrl());
            if (proxy == null) {
                System.out.println("\tproxy = null" + DateUtil.timeNow());
            } else {
                System.out.println("\tproxy = " + proxy.getHttpHost().getHostName() + DateUtil.timeNow());
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
                TimeUnit.MILLISECONDS.sleep(new Random().nextInt(2000 - 1000 + 1) + 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //加载新页面
            page = downloador.download(newRequest, set, proxy);

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
//            System.out.println(newUrl);
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
//            System.out.println(avator.toString());
            //获取评论内容，点赞数和日期
            Elements sentence = element.select("p");
            Elements votes = element.select("span.votes");
            Elements time = element.select("span.comment-time");
            comment.append(sentence.text()).append("\t(votes:").append(votes.text())
                    .append("\ttime:").append(time.attr("title")).append(")");
//            System.out.println(comment.toString());

            comments.put(avator.toString(), comment.toString());
        }

        return comments;
    }
}
