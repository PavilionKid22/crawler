package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.PageHandler.CommentFutureTask;
import cn.mvncode.webcrawler.PageHandler.PageCommentHandler;
import cn.mvncode.webcrawler.PageHandler.PageHandleFactory;
import cn.mvncode.webcrawler.PageHandler.PageListHandler;
import cn.mvncode.webcrawler.Proxy.GetProxyThread;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.Utils.CloseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * 输出到控制台
 * Created by Pavilion on 2017/3/17.
 */
public class Console implements Observer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ExecutorService commentHandleService = Executors.newFixedThreadPool(3);//commentHandler线程池
    private ExecutorService updateDataBaseService = Executors.newFixedThreadPool(3);//更新数据库线程池

    private Map<String, PageCommentHandler> pageCommentHandlerMaps = new HashMap<String, PageCommentHandler>();

    public static Map<String, ResultItem> list = new HashMap<String, ResultItem>();
    public static boolean updateCommentFlag = false;
    public static String tableName = null;

    private CrawlerSet set;
    private Request request;
    private Proxy proxy;

    private PageListHandler pageListHandler;
    private DownloadPage downloader;
    private GetProxyThread proxyThread;


    /**
     * 处理逻辑
     */
    public void process (CrawlerSet set, Request request, Proxy proxy) {

        //初始化构件
        initComponent(set, request, proxy);

        //处理网页
        //抓取url集
        logger.info("getting url list...");
        ResultItem urlList = null;
        try {
            urlList = pageListHandler.getHandler(request, set, proxy, downloader);
        } catch (IOException e) {
//            e.printStackTrace();
            logger.error("list get failed");
        }
        logger.info(Integer.toString(urlList.getFields().size()));
//        new Thread(new MoviesDataBase(urlList)).start();//写入数据库
        //抓取评论
        logger.info("getting comments...");
        for (Map.Entry<String, Object> entry : urlList.getFields().entrySet()) {
            String title = entry.getKey();
            Request seek = new Request(entry.getValue().toString());
            PageCommentHandler pageCommentHandler = getPageCommentHandler(seek, title);

            CommentFutureTask task = new CommentFutureTask(pageCommentHandler, title);
            commentHandleService.execute(task);
            try {
                TimeUnit.MILLISECONDS.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        //comment写入数据库
//        int count = 0;
//        while (true) {
//            if (updateCommentFlag) {//申请一个线程
//                logger.info("push into database now...");
//                if (tableName == null) {
//                    updateCommentFlag = false;
//                    continue;
//                }
//                CommentDataBase dataBase = new CommentDataBase(tableName, list.get(tableName));
//                updateDataBaseService.execute(dataBase);
//                count++;
//                updateCommentFlag = false;
//            }
//            if (count == urlList.getFields().size()) {
//                break;
//            }
//            try {
//                TimeUnit.MILLISECONDS.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        //关闭构件
        close();
    }

    /**
     * 获取PageCommentHandler对象
     *
     * @param seek
     * @param name
     * @return
     */
    public PageCommentHandler getPageCommentHandler (Request seek, String name) {
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
     * 初始化构件(beta0.1.0)
     * 未完成
     */
    public void initComponent (CrawlerSet set, Request request, Proxy proxy) {

        this.set = set;
        this.request = request;
        this.proxy = proxy;

        downloader = new DownloadPage();
        pageListHandler = new PageListHandler();
        proxyThread = new GetProxyThread();
        //启动代理池线程
        if (set.isLaunchProxyPool()) {
            new Thread(proxyThread).start();
            proxyThread.addObserver(this);// 该类来观察GetProxyThread实例化线程thread
        }
    }

    /**
     * 关闭构件
     */
    public void close () {
        proxyThread.close();
        commentHandleService.shutdown();
        updateDataBaseService.shutdown();
        CloseUtil.destroyEach(pageListHandler);
        CloseUtil.destroyEach(downloader);
    }


    /**
     * 观察者模式
     * 只有在setChange()被调用后，notifyObservers()才会去调用update()
     *
     * @param o
     * @param arg
     */
    @Override
    public void update (Observable o, Object arg) {
        proxy = (Proxy) arg;
    }

}
