package cn.mvncode.webcrawler.downloadpage;

import cn.mvncode.webcrawler.Request;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Pavilion on 2017/3/14.
 */
public class downloadpageTest {
    @Test
    public void getContent () throws Exception {
        new downloadpage().getContent(new Request("https://www.baidu.com/"));
    }

}