package cn.mvncode.webcrawler;

import java.util.*;

/**
 * 存储网页解析结果
 * Created by Pavilion on 2017/3/17.
 */
public class ResultItem {

    private Map<String, Object> fields = new LinkedHashMap<String, Object>();

    private Map<String, String> comment = new LinkedHashMap<String, String>();

    private Request request;

    private CrawlerSet crawlerSet;


    public Map<String, String> getComment () {
        return comment;
    }

    public ResultItem addComment (String key, String value) {
        comment.put(key, value);
        return this;
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

    public void setRequest (Request request) {
        this.request = request;
    }

    public <T> ResultItem put (String key, T value) {
        fields.put(key, value);
        return this;
    }

    public <T> T get (String key) {
        Object o = fields.get(key);
        if (o == null) {
            return null;
        }
        return (T) fields.get(key);
    }

    public Map<String, Object> getFields () {
        return fields;
    }
}
