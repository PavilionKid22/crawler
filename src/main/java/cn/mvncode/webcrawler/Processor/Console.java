package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.PageHandler.PageCommentHandler;
import cn.mvncode.webcrawler.Proxy.GetProxyThread;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.Utils.CloseUtil;
import cn.mvncode.webcrawler.Utils.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * 输出到控制台
 * Created by Pavilion on 2017/3/17.
 */
public class Console implements Observer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private CrawlerSet set;
    private Request request;
    private Proxy proxy;

    private PageCommentHandler pageCommentHandler;
    private DownloadPage downloador;
    private GetProxyThread proxyThread;

    public Console (CrawlerSet set, Request request, Proxy proxy) {
        this.set = set;
        this.request = request;
        this.proxy = proxy;
    }


    public void process () {

        //初始化构件
        initComponent();
        //处理网页
        ResultItem result = null;
        try {
            result = pageCommentHandler.getHandler(request, set, proxy, downloador);
        } catch (IOException e) {
            logger.error("pageHandler failed");
//            e.printStackTrace();
        }
        /*  测试  */
        System.out.println(result.getComment().size());
//        for (Map.Entry<String, String> view : result.getComment().entrySet()) {
//            System.out.println(view.getKey() + ":" + view.getValue() + "\n");
//        }
        //关闭构件
        close();
    }

    /**
     * 初始化构件(beta0.1.0)
     * 未完成
     *
     */
    public void initComponent () {
        if (request != null) {
            set.setDomain(UrlUtils.getDomain(UrlUtils.getDomain(request.getUrl())));
        }
        pageCommentHandler = new PageCommentHandler();
        downloador = new DownloadPage();
        proxyThread = new GetProxyThread();
        //启动代理池线程
        if(set.isLaunchProxyPool()){
            new Thread(proxyThread).start();
            proxyThread.addObserver(this);// 该类来观察GetProxyThread实例化线程thread
        }
    }

    /**
     * 关闭构件
     */
    public void close () {
//        pageCommentHandler.close();
        proxyThread.close();
        CloseUtil.destroyEach(pageCommentHandler);
        CloseUtil.destroyEach(downloador);
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
