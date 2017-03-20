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
public class Proxy implements Delayed, Serializable {

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
    private Long canReuseTime = 0L;//重用时长
    private Long lastBorrowTime = System.currentTimeMillis();
    private Long responseTime = 0L;//响应时间

    private int failedNum = 0;
    private int successNum = 0;
    private int borrowNum = 0;

    private List<Integer> fialedErrorType = new ArrayList<Integer>();

    public Proxy (HttpHost httpHost, String user, String password) {
        this.httpHost = httpHost;
        this.user = user;
        this.password = password;
        this.canReuseTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(reuseTimeInterval, TimeUnit.MILLISECONDS);
    }

    public Proxy (HttpHost httpHost, String user, String password, int reuseInterval) {
        this.httpHost = httpHost;
        this.user = user;
        this.password = password;
        this.canReuseTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(reuseInterval, TimeUnit.MILLISECONDS);
    }

    public int getSuccessNum () {
        return successNum;
    }

    public void successNumIncrement (int increment) {
        this.successNum += increment;
    }

    public Long getLastUseTime () {
        return lastBorrowTime;
    }

    public void setLastBorrowTime (Long lastBorrowTime) {
        this.lastBorrowTime = lastBorrowTime;
    }

    public void recordResponse () {
        this.responseTime = (System.currentTimeMillis() - lastBorrowTime + responseTime) / 2;
        this.lastBorrowTime = System.currentTimeMillis();
    }

    public List<Integer> getFialedErrorType () {
        return fialedErrorType;
    }

    public void setFialedErrorType (List<Integer> fialedErrorType) {
        this.fialedErrorType = fialedErrorType;
    }

    public void fail (int failedErrorType) {
        this.failedNum++;
        this.fialedErrorType.add(failedErrorType);
    }

    public int getFailedNum () {
        return failedNum;
    }

    public void setFailedNum (int failedNum) {
        this.failedNum = failedNum;
    }

    public String getFailedType () {
        String re = "";
        for (Integer i : this.fialedErrorType) {
            re += i + ". ";
        }
        return re;
    }

    public HttpHost getHttpHost () {
        return httpHost;
    }

    public int getReuseTimeInterval () {
        return reuseTimeInterval;
    }

    public void setReuseTimeInterval (int reuseTimeInterval) {
        this.reuseTimeInterval = reuseTimeInterval;
    }


    public String getUser () {
        return user;
    }

    public String getPassword () {
        return password;
    }

    @Override
    public long getDelay (TimeUnit unit) {
        return unit.convert(canReuseTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo (Delayed o) {
        Proxy that = (Proxy) o;
        return canReuseTime > that.canReuseTime ? 1 : (canReuseTime < that.canReuseTime ? -1 : 0);
    }

    @Override
    public String toString () {
        String re = String.format("host: %15s >> %5dms >> success: %-3.2f%% >> borrow: %d", httpHost.getAddress().getHostAddress(), responseTime,
                successNum * 100.0 / borrowNum, borrowNum);
        return re;
    }

    public void borrowNumIncrement (int increment) {
        this.borrowNum += increment;
    }

    public int getBorrowNum () {
        return borrowNum;
    }
}
