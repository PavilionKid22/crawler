package cn.mvncode.webcrawler.Proxy;

import cn.mvncode.webcrawler.Utils.DateUtil;

import java.io.IOException;
import java.util.Observable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pavilion on 2017/3/23.
 */
public class GetProxyThread extends Observable implements Runnable {

    private long updatePoolInterval = 1000 * 60 * 10;//ms
    private long initDelayForPool = 0;
    private long updateProxyInterval = (new Random().nextInt(60 - 30 + 1) + 30) * 1000;
    private long initDelayForGetProxy = 60 * 1000;

    private SimpleProxyPool proxyPool = new SimpleProxyPool();

    private Proxy currentProxy = null;

    private boolean flag = false;
    private boolean isRunning = true;


    public Proxy getCurrentProxy () {
        flag = false;//使用代理flag
        return currentProxy;
    }

    public void close () {
        proxyPool.close();
        flag = false;
        isRunning = false;
    }


    /**
     * 启动代理池
     */
    public void launchProxyPool () {
        Timer timer = new Timer();
        //更新代理池
        timer.schedule(new TimerTask() {
            @Override
            public void run () {
                try {
                    System.out.println("update proxy pool..." + DateUtil.timeNow());//ttttt
                    proxyPool.getProxyToPool();
                } catch (IOException e) {
//                    e.printStackTrace();
                    System.err.println("update pool failed" + DateUtil.timeNow());
                }
            }
        }, initDelayForPool, updatePoolInterval);

        //更新代理
        timer.schedule(new TimerTask() {
            @Override
            public void run () {
                try {
                    System.out.println("update proxy..." + DateUtil.timeNow());//ttttt
                    currentProxy = proxyPool.getProxy();
                    flag = true;
                } catch (IOException e) {
//                    e.printStackTrace();
                    flag = false;
                    currentProxy = null;
                    System.err.println("update proxy failed" + DateUtil.timeNow());
                }
            }
        }, initDelayForGetProxy, updateProxyInterval);
    }


    @Override
    public void run () {
        launchProxyPool();
        while (isRunning) {
            if (flag) {
                Proxy proxy = getCurrentProxy();
                if (proxy == null) {
                    System.out.println("proxy is null, use localhost");
                }
                setChanged();
                notifyObservers(proxy);//与setChanged()一起使用
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
