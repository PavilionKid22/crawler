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
        new downloadpage().getContent(new Request("http://blog.csdn.net/blogdevteam/article/details/62045843"));
//        new downloadpage().getContent(new Request("http://blog.csdn.net/butterflyloveztl/article/details/40357303"));
//        new downloadpage().getContent(new Request("https://www.baidu.com/"));
//        new downloadpage().getContent(new Request("https://temai.taobao.com/index.htm?pid=mm_12811289_2424861_70676714"));
//        new downloadpage().getContent(new Request("https://www.amazon.com/?&_encoding=UTF8&tag=cehome-pc-20&linkCode=ur2&linkId=52ac446f154b6503a59db834ef2f46c9&camp=1789&creative=9325"));
//        new downloadpage().getContent(new Request("http://www.163.com/"));
//        new downloadpage().getContent(new Request("http://www.tianya.cn/"));
//        new downloadpage().getContent(new Request("https://zhuanlan.zhihu.com/p/25743492"));

    }


}