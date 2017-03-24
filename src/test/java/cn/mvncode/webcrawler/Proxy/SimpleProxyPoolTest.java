package cn.mvncode.webcrawler.Proxy;

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
        if(proxy==null){
            System.out.println("null");
        }else {
            System.out.println(proxy.getHttpHost().getSchemeName());
            System.out.println(proxy.getHttpHost().getHostName());
            System.out.println(proxy.getHttpHost().getPort());
            System.out.println(proxy.getHttpHost().getAddress());
        }
    }

}