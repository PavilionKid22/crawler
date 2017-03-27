package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.PageHandler.PageResponseHandler;
import cn.mvncode.webcrawler.Proxy.GetProxyThread;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.Utils.CloseUtil;
import cn.mvncode.webcrawler.Utils.UrlUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

/**
 * 输出到控制台
 * Created by Pavilion on 2017/3/17.
 */
public class Console {

    private CrawlerSet set;
    private Request request;
    private Proxy proxy;

    private PageResponseHandler pageResponseHandler;
    private DownloadPage downloador;

    public Console (CrawlerSet set, Request request, Proxy proxy) {
        this.set = set;
        this.request = request;
        this.proxy = proxy;
    }


    public void process () {

        //初始化构件
        initComponent(request);
        //处理网页
        ResultItem result = null;
        try {
            result = pageResponseHandler.getHandler(request, set, proxy, downloador);
        } catch (IOException e) {
            System.err.println("pageResponseHandler failed");
//            e.printStackTrace();
        }
        /*  测试  */
        System.out.println(result.getComment().size());
        for (Map.Entry<String, String> view : result.getComment().entrySet()) {
            System.out.println(view.getKey() + ":" + view.getValue() + "\n");
        }
        //关闭构件
        close();
    }

    /**
     * 初始化构件(beta0.1.0)
     * 未完成
     *
     * @param request
     */
    public void initComponent (Request request) {
        if (request != null) {
            set.setDomain(UrlUtils.getDomain(UrlUtils.getDomain(request.getUrl())));
        }
        pageResponseHandler = new PageResponseHandler();
        downloador = new DownloadPage();
    }

    /**
     * 关闭构件
     */
    public void close () {
        pageResponseHandler.close();
        CloseUtil.destroyEach(pageResponseHandler);
        CloseUtil.destroyEach(downloador);
    }


}
