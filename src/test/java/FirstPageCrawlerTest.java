import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Pavilion on 2017/3/13.
 */
public class FirstPageCrawlerTest {
    @Test
    public void me () throws Exception {
        FirstPageCrawler.me().execute("https://www.baidu.com/");
//        FirstPageCrawler.me().execute("http://blog.csdn.net/singit/article/details/62040688");
    }

}