package cn.mvncode.webcrawler.Proxy;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pavilion on 2017/3/23.
 */
public class GetProxyThread implements Runnable {

    private int updatePoolInterval = 1000 * 60 * 60 * 12;//ms
    private int updateProxyInterval = (new Random().nextInt(90 - 30 + 1) + 30) * 1000;//ms

    private SimpleProxyPool proxyPool = new SimpleProxyPool();

    private Proxy currentProxy = null;

    private boolean flag = true;

    public Proxy getCurrentProxy () {
        return currentProxy;
    }

    public void close () {
//        proxyPool.close();
        flag = false;
    }

    @Override
    public void run () {

        Runnable updatePool = new Runnable() {
            @Override
            public void run () {
                if (!flag) {
                    Thread.currentThread().interrupt();
                }
                try {
                    proxyPool.getProxyToPool();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable updateProxy = new Runnable() {
            @Override
            public void run () {
                if (!flag) {
                    Thread.currentThread().interrupt();
                }
                try {
                    currentProxy = proxyPool.getProxy();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(updatePool, 0, updatePoolInterval, TimeUnit.MILLISECONDS);
        service.scheduleAtFixedRate(updateProxy, 10, updateProxyInterval, TimeUnit.MILLISECONDS);

    }
}
