package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.Downloadpage.Downloader;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.Utils.DataBaseUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 监视线程
 * 提交抓取评论线程
 * 监视网络
 * Created by Pavilion on 2017/4/10.
 */
public class CommentSubmitThread implements Runnable, Observer {

    private Logger logger = LoggerFactory.getLogger(CommentSubmitThread.class.getName());

    private ExecutorService commentHandleService = Executors.newFixedThreadPool(3);//commentHandler线程池

    private Map<String, PageCommentHandler> pageCommentHandlerMaps = new HashMap<String, PageCommentHandler>();

    private String baseName = "moviebase";

    private CrawlerSet set;
    private Proxy proxy;
    private Downloader downloader;

    private boolean pauseFlag;
    private Queue<Object> observableObjs = new ArrayDeque<>();

    private Downloader testDownloader = new DownloadPage();
    private Request testRequest = new Request("https://movie.douban.com/");
    private Page testPage = null;

    public CommentSubmitThread (CrawlerSet set, Proxy proxy, Downloader downloader) {
        this.set = set;
        this.proxy = proxy;
        this.downloader = downloader;
        pauseFlag = false;
    }

    /**
     * 提交集
     * 从数据库获取
     *
     * @return
     */
    public Map<String, String> getUrlList () {
        Map<String, String> urlList = new HashMap<>();
        Map<String, String> allUrlList = null;
        allUrlList = DataBaseUtil
                .getUrlList(baseName, "movies", "Title", "Url");
        for (Map.Entry<String, String> entry : allUrlList.entrySet()) {
            String tmpTableName = "tb_" + entry.getKey();
            if (!DataBaseUtil.exitTable(baseName, tmpTableName)) {
                urlList.put(entry.getKey(), entry.getValue());
            }
        }
        CommentPushDatabase.submitThreadCount = urlList.size();
        return urlList;
    }

    /**
     * 提交任务到线程池
     */
    private void submitThread () {
        Map<String, String> urlList = getUrlList();
        logger.info("submit size is " + urlList.size());
        for (Map.Entry<String, String> entry : urlList.entrySet()) {
            String title = entry.getKey();
            Request seek = new Request(entry.getValue());
            PageCommentHandler pageCommentHandler = getPageCommentHandler(seek, title);
            pageCommentHandler.addObserver(this);//注册监听者
            CommentFutureTask task = new CommentFutureTask(pageCommentHandler);//包装线程
            commentHandleService.execute(task);
        }
        logger.info("submitThread over");
        commentHandleService.shutdown();
    }

    /**
     * 获取PageCommentHandler对象
     *
     * @param seek
     * @param name
     * @return
     */
    private PageCommentHandler getPageCommentHandler (Request seek, String name) {
        if (pageCommentHandlerMaps.isEmpty()) {
            return PageHandleFactory.createPageCommentHandler(seek, set, proxy, downloader, name);
        }
        PageCommentHandler pageCommentHandler = pageCommentHandlerMaps.get(name);
        if (pageCommentHandler == null) {
            synchronized (this) {
                pageCommentHandler = new PageCommentHandler(seek, set, proxy, downloader, name);
                pageCommentHandlerMaps.put(name, pageCommentHandler);
            }
        }
        return pageCommentHandler;
    }

    /**
     * 测试页面是否可以访问(bug)
     *
     * @return
     */
    private boolean checkPage () {
        boolean flag = false;
        testPage = testDownloader.download(testRequest, set, null);
        String response = "…你访问豆瓣的方式有点像机器人程序。为了保护用户的数据，请向我们证明你是人类:";
        int code = testPage.getStatusCode();
        if (!testPage.getPlainText().isEmpty()) {
            Document document = Jsoup.parse(testPage.getPlainText());
            String text = document.select("h2").text();
            if (!text.equals(response) && code == 200) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public void run () {

        logger.info("start get comment");
        submitThread();
        while (true) {
            if (commentHandleService.isTerminated()) {
                logger.info("all submit thread terminated, exit");
                break;
            }
            if (pauseFlag) {//线程故障处理
                if (checkPage()) {
                    for (int i = 0; i < observableObjs.size(); i++) {
                        Object obj = observableObjs.poll();
                        synchronized (obj) {
                            obj.notify();
                            pauseFlag = false;
                            logger.info("continue crawl comment");
                        }
                    }
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(new Random().nextInt(6000 - 4000 + 1) + 4000);//每4到6秒检测一次
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void update (Observable o, Object arg) {
        pauseFlag = true;
        observableObjs.add(arg);//从PageCommentHandler传入对象(处理ip被封)
    }

}
