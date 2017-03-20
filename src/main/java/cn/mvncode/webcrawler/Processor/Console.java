package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.PageHandler.PageResponseHandler;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;

import java.io.IOException;
import java.util.Map;

/**
 * 输出到控制台
 * Created by Pavilion on 2017/3/17.
 */
public class Console {

    private CrawlerSet set;

    public Console (CrawlerSet set) {
        this.set = set;
    }

    /**
     * @param request
     */
    public void process (Request request) {
        //下载网页
        Page initPage = new DownloadPage().download(request, set);
        //处理网页
        PageResponseHandler pageResponseHandler = new PageResponseHandler();
        ResultItem result = null;
        try {
            result = pageResponseHandler.getHandler(initPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*  测试  */
        System.out.println(result.getComment().size());
        for (Map.Entry<String, String> view : result.getComment().entrySet()) {
            System.out.println(view.getKey() + ":" + view.getValue() + "\n");
        }
    }


}
