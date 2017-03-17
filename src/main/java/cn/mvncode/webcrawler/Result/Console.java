package cn.mvncode.webcrawler.Result;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.PageHandler.PageResponseHandler;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.downloadpage.downloadpage;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

/**
 * 输出到控制台
 * Created by Pavilion on 2017/3/17.
 */
public class Console {

    /**
     * @return Console
     */
    public static Console out () {
        return new Console();
    }

    /**
     * @param request
     * @param set
     */
    public void process (Request request, CrawlerSet set) {
        //下载网页
        Page page = new downloadpage().download(request, set);
        //处理网页
        PageResponseHandler pageResponseHandler = new PageResponseHandler(page);
        HttpResponse httpResponse = page.getHttpResponse();
        ResultItem result = null;
        try {
            result = pageResponseHandler.handleResponse(httpResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, Object> view : result.getFields().entrySet()) {
            System.out.println("Key: " + view.getKey());
            System.out.println("Value: " + view.getValue());
        }
    }


}
