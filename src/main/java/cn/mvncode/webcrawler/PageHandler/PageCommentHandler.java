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
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 网页解析
 * 抓取评论
 * Created by Pavilion on 2017/3/16.
 */
public class PageCommentHandler extends Observable implements Callable<ResultItem>, PageResponseHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

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
        resultItem.setTitle(name);
        set.setDomain(seek.getUrl());
    }

    /**
     * 获取网页处理结果
     *
     * @return result
     */
    @Override
    public ResultItem getHandler (Request seek, CrawlerSet set, Proxy proxy, Downloader downloader) {
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
        String checkStr = "还没有人写过短评";
        if (elements.text().equals(checkStr)) {
            return null;
        }
        for (Element element : elements) {
            StringBuffer comment = new StringBuffer();
            StringBuffer avator = new StringBuffer();
            //获取用户名和id
            Elements name = element.select("a[title]");
            avator.append(element.attr("data-cid"))
                    .append("\t").append(name.attr("title"));

            //获取评论内容，点赞数和日期
            Elements sentence = element.select("p");
            Elements votes = element.select("span.votes");
            Elements time = element.select("span.comment-time");
            //推荐星数
            String[] split = element.select("span[title]").toString().split("\n");
            String star = "null";
            if (split.length > 1) {
                String tmpStr = "allstar";
                int index = StringUtils.indexOf(split[0], tmpStr) + tmpStr.length();
                star = split[0].substring(index, index + 2);
            }
            comment.append(votes.text())
                    .append("\t").append(star)
                    .append("\t").append(time.attr("title"))
                    .append("\t").append(sentence.text());

            comments.put(avator.toString(), comment.toString());
        }

        return comments;
    }

    /**
     * 判断抓取是否需要人工干预
     *
     * @param page
     * @return
     */
    private boolean getError (Page page) {
        boolean flag = false;
        String response = "…你访问豆瓣的方式有点像机器人程序。为了保护用户的数据，请向我们证明你是人类:";
        int statusCode = page.getStatusCode();
        if (page.getPlainText() != null) {
            Document document = Jsoup.parse(page.getPlainText());//传入参数不能为null,否则会杀死线程
            Elements h2 = document.select("h2");
            if (statusCode == 403 && h2.text().equals(response)) {
                return true;
            }
        }
        return flag;
    }


    @Override
    public ResultItem call () throws Exception {
        //初始化page
        handleResponse(seek, set, downloader);
        //初始化refer
        refer = page.getUrl();
        //解析网页
        while (true) {
            logger.info("Url = " + page.getUrl() + "\t" + name + "\t" + resultItem.getComment().size());
            if (proxy != null) {
                logger.info("proxy = " + proxy.getHttpHost().getHostName());
            }

            //格式化html
            Document document = Jsoup.parse(page.getPlainText());
            //获取内容
            Map<String, String> comments = getComments(document);
            if (comments == null) break;
            for (Map.Entry<String, String> entry : comments.entrySet()) {
                resultItem.addComment(entry.getKey(), entry.getValue());
            }
            //获取下一页url
            Request newRequest = getUrl(document, refer);
            String referStr = refer.substring(0, StringUtils.indexOf(refer, "?status=P")) + "/";
            if (newRequest.getUrl().equals(referStr)) break;
            //设置refer
            newRequest.setRefer(page.getUrl());
            //删除当前处理url
            page.removeTargetUrl(page.getRequest());
            //点击新页面
            page = downloader.download(newRequest, set, proxy);
            //测试网页是否需要人工干预(ip被封)
            if (getError(page)) {
                logger.error("Please Enter Verification Code");
                synchronized (this) {
                    setChanged();
                    notifyObservers(this);//将该对象传给监听者
                    wait();//挂起线程等待响应
                }
                page = downloader.download(newRequest, set, proxy);//重新点击页面
            }
            //抓取间隔
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(3000 - 1000 + 1) + 1000);
            //检验是否关闭线程
            if (page.getStatusCode() != 200) break;
        }
        logger.info(name + ": comment catch over");
        logger.info(name + ": comments size is " + resultItem.getComment().size());
        return resultItem;
    }

}
