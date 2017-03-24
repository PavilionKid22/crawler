package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.PageHandler.PageResponseHandler;
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

    private PageResponseHandler pageResponseHandler;

    public Console (CrawlerSet set) {
        this.set = set;
    }

    /**
     * @param request
     */
    public void process (Request request) {

        //初始化构建
        initComponent(request);
        //处理网页
        pageResponseHandler = new PageResponseHandler();
        ResultItem result = null;
        try {
            result = pageResponseHandler.getHandler(request, set);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*  测试  */
        System.out.println(result.getComment().size());
        for (Map.Entry<String, String> view : result.getComment().entrySet()) {
            System.out.println(view.getKey() + ":" + view.getValue() + "\n");
        }
        //关闭构建
        close();
    }

    /**
     * 初始化构建(beta0.1.0)
     * 未完成
     *
     * @param request
     */
    public void initComponent (Request request) {
        if (request != null) {
            set.setDomain(UrlUtils.getDomain(request.getUrl()));
        }
    }

    /**
     * 关闭io流
     */
    public void close () {
        CloseUtil.destroyEach(pageResponseHandler);
    }



}
