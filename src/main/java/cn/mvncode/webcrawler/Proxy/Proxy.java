package cn.mvncode.webcrawler.Proxy;

import org.apache.http.HttpHost;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pavilion on 2017/3/16.
 */
public class Proxy implements Delayed,Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 1L;
    public static final int ERROR_403 = 403;
    public static final int ERROR_404 = 404;
    public static final int ERROR_BANNED = 10000;//被墙
    public static final int ERROR_Proxy = 10001;//代理失败
    public static final int SUCCESS = 200;

    private final HttpHost httpHost;
    private String user;
    private String password;

    private int reuseTimeInterval = 1500;//间隔
    private Long canReuseTime = 0L;
    private Long lastBorrowTime = System.currentTimeMillis();
    private Long responseTime = 0L;

    private int failedNum = 0;
    private int successNum = 0;
    private int borrowNum = 0;

    private List<Integer> fialedErrorType = new ArrayList<Integer>();


    public Proxy (HttpHost httpHost, String user, String password, Long canReuseTime) {
        this.httpHost = httpHost;
        this.user = user;
        this.password = password;
        this.canReuseTime = System.nanoTime()+TimeUnit.NANOSECONDS.convert(reuseTimeInterval,TimeUnit.MILLISECONDS);
    }

    public String getUser () {
        return user;
    }

    public String getPassword () {
        return password;
    }

    @Override
    public long getDelay (TimeUnit unit) {
        return 0;
    }

    @Override
    public int compareTo (Delayed o) {
        return 0;
    }
}
