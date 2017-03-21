package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.Utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

/**
 * 网页解析
 * Created by Pavilion on 2017/3/16.
 */
public class PageResponseHandler {

    private static ResultItem resultItem = new ResultItem();

    /**
     * 获取网页处理结果
     *
     * @return result
     */
    public ResultItem getHandler (Request seek, CrawlerSet set) throws IOException {
        return new PageResponseHandler().handleResponse(seek, set);
    }


    /**
     * 处理网页(beta0.1.0)
     *
     * @param seek
     * @param set
     * @return
     * @throws IOException
     */
    public ResultItem handleResponse (Request seek, CrawlerSet set) throws IOException {

        //初始化下载器
        DownloadPage downloadPage = new DownloadPage();
        Page page = downloadPage.download(seek, set);
        String refer = page.getUrl();

        while (!page.getTargetUrls().isEmpty()) {

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
            /*  测试  */
            System.out.println("Request URL: " + page.getUrl());
            System.out.println("Status Code: " + page.getStatusCode());
            /*  测试  */
            //获取下一页url
            Request newRequest = getUrl(document, refer);
            //获取refer
            newRequest.setRefer(page.getUrl());
            //删除当前处理url
            page.removeTargetUrl(page.getRequest());
            //加载新页面
            page = downloadPage.download(newRequest, set);
            //抓取间隔
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        return resultItem;
    }

    /**
     * 提取网页中包含的所有链接(beta0.1.0)
     *
     * @param document
     * @return
     */
    private Set<Request> getUngrapUrl (Document document) {

        Set<Request> ungrapUrl = new HashSet<Request>();//简单去重

        Elements links = document.select("a[href]");//带有href属性的a元素
        Elements medias = document.select("[src]");//带有src属性的所有元素
        Elements imports = document.select("link[href]");//带有href属性的link元素

        for (Element link : links) {
            if (!link.attr("abs:href").isEmpty()) {
                ungrapUrl.add(new Request(link.attr("abs:href")));
            }
        }

        for (Element media : medias) {
            if (!media.attr("abs:src").isEmpty()) {
                ungrapUrl.add(new Request(media.attr("abs:src")));
            }
        }

        for (Element element : imports) {
            if (!element.attr("abs:href").isEmpty()) {
                ungrapUrl.add(new Request(element.attr("abs:href")));
            }
        }

        return ungrapUrl;
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
