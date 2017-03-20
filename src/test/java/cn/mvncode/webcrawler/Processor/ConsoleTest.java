package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Request;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Pavilion on 2017/3/19.
 */
public class ConsoleTest {
    @Test
    public void process () throws Exception {
        CrawlerSet set = CrawlerSet.setByDefault();
        new Console(set).process(new Request("https://movie.douban.com/subject/26648249/comments?status=P"));
    }

}