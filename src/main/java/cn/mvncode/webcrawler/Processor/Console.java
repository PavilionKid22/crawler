package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.PageHandler.CommentSubmitThread;
import cn.mvncode.webcrawler.PageHandler.PageListHandler;
import cn.mvncode.webcrawler.Proxy.GetProxyThread;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.Utils.CloseUtil;

import java.util.Observable;
import java.util.Observer;

/**
 * 输出到控制台
 * Created by Pavilion on 2017/3/17.
 */
public class Console implements Observer {

    private CrawlerSet set;
    private Request request;
    private Proxy proxy;

    private PageListHandler pageListHandler;
    private DownloadPage downloader;
    private GetProxyThread proxyThread;

    public Console (CrawlerSet set, Request request, Proxy proxy) {
        this.set = set;
        this.request = request;
        this.proxy = proxy;
    }

    /**
     * 处理逻辑
     */
    public void process () {

        //初始化构件
        initComponent();

        //处理网页
        //抓取url集
        ResultItem urlList = pageListHandler.getHandler(request, set, proxy, downloader);
        //更新数据库表movies
        new Thread(new MoviesDataBase(urlList)).start();
        //抓取评论
        new Thread(new CommentSubmitThread(set, proxy, downloader)).start();
        //写入数据库
//        Thread pushDatabaseThread = new Thread(new CommentPushDatabase());
//        try {
//            pushDatabaseThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        //关闭构件
        close();
    }

    /**
     * 初始化构件(beta0.1.0)
     * 未完成
     */
    public void initComponent () {
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
