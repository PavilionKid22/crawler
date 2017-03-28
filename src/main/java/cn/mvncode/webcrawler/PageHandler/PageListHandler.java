package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.DownloadAjaxPage;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.Downloadpage.Downloader;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.ResultItem;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Pavilion on 2017/3/28.
 */
public class PageListHandler extends PageResponseHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取结果
     *
     * @param seek
     * @param set
     * @param proxy
     * @param downloader
     * @return
     * @throws IOException
     */
    @Override
    public ResultItem getHandler (Request seek, CrawlerSet set, Proxy proxy, Downloader downloader) throws IOException {
        this.proxy = proxy;
        handleResponse(seek, set, downloader);
        return resultItem;
    }

    /**
     * 解析网页
     *
     * @param seek
     * @param set
     * @param downloader
     * @throws IOException
     */
    @Override
    public void handleResponse (Request seek, CrawlerSet set, Downloader downloader) throws IOException {

        Page page = downloader.download(seek, set, proxy);


    }

    /**
     * 获取名单列表url
     *
     * @param document
     * @return
     */
    private Request getListUrl (Document document) {

        Request url = new Request();

        return url;
    }

}
