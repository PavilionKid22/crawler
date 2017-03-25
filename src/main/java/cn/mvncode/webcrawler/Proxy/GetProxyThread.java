package cn.mvncode.webcrawler.Proxy;

import cn.mvncode.webcrawler.Utils.DateUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Pavilion on 2017/3/23.
 */
public class GetProxyThread {

    private int updatePoolInterval;
    private int initDelayForPool;
    private int updateProxyInterval;
    private int initDelayForGetProxy;

    private SimpleProxyPool proxyPool = new SimpleProxyPool();

    private Proxy currentProxy = null;

    private boolean flag = false;

    public GetProxyThread () {
        updateProxyInterval = (new Random().nextInt(90 - 60 + 1) + 60) * 1000;//ms
        updatePoolInterval = 1000 * 60 * 10;//ms
        initDelayForGetProxy = initDelayForPool + updateProxyInterval;
        initDelayForPool = 0;
        launchProxyPool();
    }

    public boolean isFlag () {
        return flag;
    }

    public Proxy getCurrentProxy () {
        flag = false;//使用代理flag
        return currentProxy;
    }

    public void close () {
        proxyPool.close();
        flag = false;
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
                    System.out.println("更新代理池" + DateUtil.timeNow());//ttttt
                    proxyPool.getProxyToPool();
                } catch (IOException e) {
//                    e.printStackTrace();
                    System.err.println("更新线程池失败" + DateUtil.timeNow());
                }
            }
        }, initDelayForPool, updatePoolInterval);

        //更新代理
        timer.schedule(new TimerTask() {
            @Override
            public void run () {
                try {
                    System.out.println("更新代理" + DateUtil.timeNow());//ttttt
                    currentProxy = proxyPool.getProxy();
                    flag = true;
                } catch (IOException e) {
//                    e.printStackTrace();
                    flag = false;
                    System.err.println("获取代理失败" + DateUtil.timeNow());
                }
            }
        }, initDelayForGetProxy, updateProxyInterval);
    }


}
