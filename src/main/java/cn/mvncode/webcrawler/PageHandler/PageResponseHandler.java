package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.ResultItem;

import java.io.IOException;

/**
 * Created by Pavilion on 2017/3/28.
 */
public abstract class PageResponseHandler {

    public static ResultItem resultItem;
    public static Proxy proxy;

    abstract ResultItem getHandler (Request seek, CrawlerSet set, Proxy proxy, DownloadPage downloadPage) throws IOException;

    abstract void handleResponse (Request seek, CrawlerSet set, DownloadPage downloador) throws IOException;

}
