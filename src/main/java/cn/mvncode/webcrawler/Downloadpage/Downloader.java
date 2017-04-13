package cn.mvncode.webcrawler.Downloadpage;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;

import java.io.IOException;

/**
 * Created by Pavilion on 2017/3/28.
 */
public abstract class Downloader {

    public abstract Page download(Request request, CrawlerSet crawlerSet, Proxy proxy);

}
