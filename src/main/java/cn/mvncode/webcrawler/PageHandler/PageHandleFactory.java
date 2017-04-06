package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.Downloader;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;

/**
 * Created by Pavilion on 2017/4/6.
 */
public class PageHandleFactory {

    public static PageCommentHandler createPageCommentHandler (Request seek, CrawlerSet set, Proxy proxy, Downloader downloader, String name) {
        return new PageCommentHandler(seek, set, proxy, downloader, name);
    }

}
