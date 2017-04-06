package cn.mvncode.webcrawler.Processor;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Request;
import org.junit.Test;

/**
 * Created by Pavilion on 2017/3/19.
 */
public class ConsoleTest {
    @Test
    public void process () throws Exception {
        CrawlerSet set = CrawlerSet.setDefault().addCookie("Cookie", "ll=\"118172\"; bid=ubEjM5_AvE0; ps=y; ue=\"15957119500@163.com\"; " +
                "dbcl2=\"159332631:evWlNwMx2aM\"; ck=rER4; ct=y; _vwo_uuid_v2=463A2B5EA46184CF16D87860722C39A5|93d29d8a851a42fed9a454655491e5d3;" +
                " __utmt=1; ap=1; push_noty_num=0; push_doumail_num=0; _pk_id.100001.4cf6=47a333483025b220.1490011191.4.1490078618.1490066186.;" +
                " _pk_ses.100001.4cf6=*; __utma=30149280.1053201544.1490011191.1490064491.1490077120.4; __utmb=30149280.8.10.1490077120; " +
                "__utmc=30149280; __utmz=30149280.1490011191.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utmv=30149280.15933; " +
                "__utma=223695111.451825191.1490011191.1490064491.1490077137.4; __utmb=223695111.0.10.1490077137; __utmc=223695111; " +
                "__utmz=223695111.1490011191.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
        new Console().process(set, new Request("https://movie.douban.com/subject/26648249/comments?status=P"), null);
    }

}