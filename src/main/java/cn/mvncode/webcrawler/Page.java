package cn.mvncode.webcrawler;

import cn.mvncode.webcrawler.Utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 存储下载的页面
 * Created by Pavilion on 2017/3/14.
 */
public class Page {

    private Request request;

    private HttpResponse httpResponse;

    private String plainText;

    private String url;

    private int statusCode;

    private String charset;

    private CrawlerSet crawlerSet;

    private Set<Request> targetUrls = new HashSet<Request>();

    public Page () {
        plainText = null;
        url = null;
        statusCode = 0;
        charset = "utf-8";
    }

    public Page addTargetUrl(Request request){
        targetUrls.add(request);
        return this;
    }

    public Page removeTargetUrl(Request request){
        targetUrls.remove(request);
        return this;
    }

    public Set<Request> getTargetUrls () {
        return targetUrls;
    }

    public CrawlerSet getCrawlerSet () {
        return crawlerSet;
    }

    public void setCrawlerSet (CrawlerSet crawlerSet) {
        this.crawlerSet = crawlerSet;
    }

    public Request getRequest () {
        return request;
    }

    public HttpResponse getHttpResponse () {
        return httpResponse;
    }

    public String getCharset () {
        return charset;
    }

    public void setCharset (String charset) {
        this.charset = charset;
    }

    public void setHttpResponse (HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public void setRequest (Request request) {
        this.request = request;
    }

    public String getPlainText () {
        return plainText;
    }

    public void setPlainText (String plainText) {
        this.plainText = plainText;
    }

    public String getUrl () {
        return url;
    }

    public void setUrl (String url) {
        this.url = url;
    }

    public int getStatusCode () {
        return statusCode;
    }

    public void setStatusCode (int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString () {
        return "Page{" +
                "request=" + request +
                ", statusCode='" + statusCode + '\'' +
                '}';
    }

}
