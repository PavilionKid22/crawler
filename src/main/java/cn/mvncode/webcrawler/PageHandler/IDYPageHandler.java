package cn.mvncode.webcrawler.PageHandler;

import org.jsoup.nodes.Document;

/**
 * Created by Pavilion on 2017/3/19.
 */
public interface IDYPageHandler {

    public <T> T getUrl (Document document, String refer);

}
