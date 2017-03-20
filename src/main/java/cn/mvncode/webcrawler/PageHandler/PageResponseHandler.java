package cn.mvncode.webcrawler.PageHandler;

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
    public ResultItem getHandler (Page page) throws IOException {
        resultItem.setRequest(page.getRequest());
        resultItem.setCrawlerSet(page.getCrawlerSet());
        return new PageResponseHandler().handleResponse(page);
    }


    /**
     * 处理网页
     *
     * @param initPage
     * @return
     * @throws IOException
     */
    public ResultItem handleResponse (Page initPage) throws IOException {

        resultItem = grapProcess(initPage);

        return resultItem;
    }

    /**
     * 抓取进程
     * 递归调用
     *
     * @param page
     */
    private ResultItem grapProcess (Page page) throws HttpResponseException {

        StatusLine statusLine = page.getHttpResponse().getStatusLine();
        if (statusLine.getStatusCode() >= 300) {
//            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
            return resultItem;
        }
        //格式化html
        Document document = Jsoup.parse(page.getPlainText());
        //获取内容
        Map<String, String> comments = getComments(document);
        for (Map.Entry<String, String> entry : comments.entrySet()) {
            resultItem.addComment(entry.getKey(), entry.getValue());
//            System.out.println(entry.getKey() + ":" + entry.getValue() + "\n");
        }
        //获取下一页url
        Request request = getUrl(document, page.getUrl());
        System.out.println(request.getUrl());
        if (!request.equals(resultItem.getRequest()) && request.getUrl() != null) {
            resultItem.setRequest(request);
            resultItem = resultItem.addTargetUrls(request);//存入容器
            Page newPage = new DownloadPage().download(request, resultItem.getCrawlerSet());
            resultItem = grapProcess(newPage);
        }
        return resultItem;
    }


    /**
     * 提取网页中包含的所有链接
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
            String newUrl = refer + "/" + link.attr("href");
            url.setUrl(newUrl);
//            System.out.println(newUrl);
        }

        return url;
    }

    /**
     * 通过key获取目标urls
     *
     * @param resultItem
     * @param key
     * @return
     */
    private String selectUrl (ResultItem resultItem, String key) {
        Set<Request> targetUrls = resultItem.getTargetUrls();
        String refer = resultItem.getRequest().getUrl();
        String subUrl = UrlUtils.getSuburl(refer, targetUrls, key);
        if (StringUtils.isEmpty(subUrl)) {
            subUrl = refer + "/" + key;
            return subUrl;
        } else {
            resultItem.deleteTargetUrls(new Request(subUrl));
            return subUrl;
        }
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
