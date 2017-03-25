package cn.mvncode.webcrawler.Proxy;

import org.apache.http.HttpHost;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pavilion on 2017/3/16.
 */
public class Proxy implements Serializable, Delayed {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 1L;

    private long reuseTimeInterval = 2000;//ms

    private final HttpHost httpHost;

    public Proxy (HttpHost httpHost) {
        this.httpHost = httpHost;
        //转为ns
        reuseTimeInterval = System.nanoTime() + TimeUnit.NANOSECONDS.convert(reuseTimeInterval, TimeUnit.NANOSECONDS);
    }

    public void resetInterval(){
        reuseTimeInterval = System.nanoTime() + TimeUnit.NANOSECONDS.convert(reuseTimeInterval, TimeUnit.NANOSECONDS);
    }

    public HttpHost getHttpHost () {
        return httpHost;
    }

    @Override
    public long getDelay (TimeUnit unit) {
        return unit.convert(reuseTimeInterval - System.nanoTime(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo (Delayed o) {
        Proxy that = (Proxy) o;
        return reuseTimeInterval > that.reuseTimeInterval ? 1 : (reuseTimeInterval < that.reuseTimeInterval ? -1 : 0);
    }


}
