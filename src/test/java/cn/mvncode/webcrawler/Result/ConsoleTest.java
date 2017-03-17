package cn.mvncode.webcrawler.Result;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Request;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Pavilion on 2017/3/17.
 */
public class ConsoleTest {
    @Test
    public void out () throws Exception {
        new Console().out().process(new Request("http://ent.firefox.sina.com/17/0317/11/LPBKRF71JU38OID4.html"),
                CrawlerSet.set().setRetryTimes(3).setTimeOut(5000));
    }

}