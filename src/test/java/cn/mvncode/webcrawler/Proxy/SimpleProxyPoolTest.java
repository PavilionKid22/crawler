package cn.mvncode.webcrawler.Proxy;

import cn.mvncode.webcrawler.Utils.DateUtil;
import org.junit.Test;

/**
 * Created by Pavilion on 2017/3/23.
 */
public class SimpleProxyPoolTest {
    @Test
    public void getProxy () throws Exception {
        SimpleProxyPool pool = new SimpleProxyPool();
        pool.getProxyToPool();
        Proxy proxy = pool.getProxy();
        if (proxy == null) {
            System.out.println("null" + DateUtil.timeNow());
        } else {
            System.out.println(proxy.getHttpHost().getHostName() + DateUtil.timeNow());
        }
    }

}