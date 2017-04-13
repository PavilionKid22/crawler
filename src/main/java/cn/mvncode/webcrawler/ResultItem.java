package cn.mvncode.webcrawler;

import java.util.*;

/**
 * 存储网页解析结果
 * Created by Pavilion on 2017/3/17.
 */
public class ResultItem {

    private Map<String, Object> fields = new LinkedHashMap<String, Object>();//存储目标url集(title, url)

    private Map<String, String> comment = new LinkedHashMap<String, String>();//存储评论内容(用户+ID, 评论内容+点赞数+日期+推荐星数)

    private Request request;

    private CrawlerSet crawlerSet;

    private String title;

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

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

    public <T> ResultItem putField (String key, T value) {
        fields.put(key, value);
        return this;
    }

    public <T> T getField (String key) {
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
