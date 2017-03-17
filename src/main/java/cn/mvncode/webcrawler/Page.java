package cn.mvncode.webcrawler;

import cn.mvncode.webcrawler.Utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储下载的页面
 * Created by Pavilion on 2017/3/14.
 */
public class Page {

    private Request request;

    private HttpResponse httpResponse;

    public Page () {
    }

    public void setRequest (Request request) {
        this.request = request;
    }

    public HttpResponse getHttpResponse () {
        return httpResponse;
    }

    public void setHttpResponse (HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public Request getRequest () {
        return request;
    }


    @Override
    public String toString () {
        return "Page{" +
                "request=" + request +
                ", httpResponse=" + httpResponse +
                '}';
    }
}
