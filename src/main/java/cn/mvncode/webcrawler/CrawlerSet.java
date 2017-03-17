package cn.mvncode.webcrawler;

import cn.mvncode.webcrawler.Utils.UrlUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.UsernamePasswordCredentials;

import java.util.*;

/**
 * 爬虫设置
 * Created by Pavilion on 2017/3/15.
 */
public class CrawlerSet {

    private String domain;//网址

    private String userAgent;//请求名

    private Map<String, String> defaultCookies = new LinkedHashMap<String, String>();//该网页Cookie

    private Map<String, Map<String, String>> cookies = new HashMap<String, Map<String, String>>();//所有网页的Cookies

    private String charset;

    /**
     * 初始请求链接
     */
    private List<Request> startRequests = new ArrayList<Request>();

    private int sleepTime = 5000;

    private int retryTimes = 0;

    private int cycleRetryTimes = 0;

    private int timeOut = 5000;

    private int retrySleepTime = 1000;

    private static final Set<Integer> DEFAULT_STATUS_CODE_SET = new HashSet<Integer>();

    private Set<Integer> acceptStatusCode = DEFAULT_STATUS_CODE_SET;

    private Map<String, String> headers = new HashMap<String, String>();

    /**
     * 代理
     */
    private HttpHost httpProxy;

    private UsernamePasswordCredentials usernamePasswordCredentials;//代理用户名密码设置

//    private ProxyPool httpProxyPool;

    private boolean useGzip = true;

    /**
     * 初始化DEFAULT_STATUS_CODE_SET
     */
    static {
        DEFAULT_STATUS_CODE_SET.add(200);
    }

    public static CrawlerSet set(){
        return new CrawlerSet();
    }

    /**
     * 从网址中添加Cookie
     *
     * @param name
     * @param value
     * @return this
     */
    public CrawlerSet addCookie (String name, String value) {
        defaultCookies.put(name, value);
        return this;
    }

    /**
     * 从其他网址添加Cookie
     *
     * @param domain
     * @param name
     * @param value
     * @return this
     */
    public CrawlerSet addCookie (String domain, String name, String value) {
        if (!cookies.containsKey(domain)) {
            cookies.put(domain, new HashMap<String, String>());
        }
        return this;
    }

    /**
     * 设置请求名
     *
     * @param userAgent
     * @return this
     */
    public CrawlerSet setUserAgent (String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    /**
     * 获取Cookie
     *
     * @return defaultCookies
     */
    public Map<String, String> getDefaultCookies () {
        return defaultCookies;
    }

    /**
     * 获取所有网页cookies
     *
     * @return cookies
     */
    public Map<String, Map<String, String>> getCookies () {
        return cookies;
    }

    /**
     * 获取请求名
     *
     * @return userAgent
     */
    public String getUserAgent () {
        return userAgent;
    }

    /**
     * 获取网址
     *
     * @return domain
     */
    public String getDomain () {
        return domain;
    }

    /**
     * 添加网址
     *
     * @param domain
     */
    public void setDomain (String domain) {
        this.domain = domain;
    }

    /**
     * 设置网页编码
     *
     * @param charset
     * @return
     */
    public CrawlerSet setCharset (String charset) {
        this.charset = charset;
        return this;
    }

    /**
     * 获取网页编码
     *
     * @return
     */
    public String getCharset () {
        return charset;
    }


    public int getTimeOut () {
        return timeOut;
    }

    /**
     * 设定超时
     *
     * @param timeOut
     * @return
     */
    public CrawlerSet setTimeOut (int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    /**
     * 设定接收状态码
     * 默认200
     *
     * @param acceptStatusCode
     * @return
     */
    public CrawlerSet setAcceptStatusCode (Set<Integer> acceptStatusCode) {
        this.acceptStatusCode = acceptStatusCode;
        return this;
    }

    /**
     * 获取状态码
     *
     * @return
     */
    public Set<Integer> getAcceptStatusCode () {
        return acceptStatusCode;
    }

    /**
     * 获取初始请求链接集
     *
     * @return
     */
    public List<String> getStartUrls () {
        return UrlUtils.convertToUrls(startRequests);
    }

    public List<Request> getStartRequests () {
        return startRequests;
    }

    /**
     * 添加一个url进入初始url集
     *
     * @param startUrl
     * @return
     */
    public CrawlerSet addStartUrl (String startUrl) {
        startRequests.add(new Request(startUrl));
        return this;
    }

    public int getSleepTime () {
        return sleepTime;
    }

    /**
     * 设置处理不同页面的间隔
     *
     * @param sleepTime
     * @return
     */
    public CrawlerSet setSleepTime (int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    /**
     * 获取重试次数
     * 默认0次
     *
     * @return
     */
    public int getRetryTimes () {
        return retryTimes;
    }

    public Map<String, String> getHeaders () {
        return headers;
    }

    /**
     * 添加请求头
     *
     * @param key
     * @param value
     * @return
     */
    public CrawlerSet addHeader (String key, String value) {
        headers.put(key, value);
        return this;
    }

    /**
     * 设定重试次数
     * 默认0次
     *
     * @param retryTimes
     * @return
     */
    public CrawlerSet setRetryTimes (int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public int getCycleRetryTimes () {
        return cycleRetryTimes;
    }

    /**
     * 设置循环重试次数
     * @param cycleRetryTimes
     * @return
     */
    public CrawlerSet setCycleRetryTimes (int cycleRetryTimes) {
        this.cycleRetryTimes = cycleRetryTimes;
        return this;
    }

    /**
     * 设置代理
     *
     * @param httpProxy
     */
    public void setHttpProxy (HttpHost httpProxy) {
        this.httpProxy = httpProxy;
    }

    public HttpHost getHttpProxy () {
        return httpProxy;
    }

    public boolean isUseGzip () {
        return useGzip;
    }

    public int getRetrySleepTime () {
        return retrySleepTime;
    }

    public CrawlerSet setRetrySleepTime (int retrySleepTime) {
        this.retrySleepTime = retrySleepTime;
        return this;
    }

    public CrawlerSet setUseGzip (boolean useGzip) {
        this.useGzip = useGzip;
        return this;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrawlerSet crawlerSet = (CrawlerSet) o;

        if (cycleRetryTimes != crawlerSet.cycleRetryTimes) return false;
        if (retryTimes != crawlerSet.retryTimes) return false;
        if (sleepTime != crawlerSet.sleepTime) return false;
        if (timeOut != crawlerSet.timeOut) return false;
        if (acceptStatusCode != null ? !acceptStatusCode.equals(crawlerSet.acceptStatusCode) : crawlerSet.acceptStatusCode != null)
            return false;
        if (charset != null ? !charset.equals(crawlerSet.charset) : crawlerSet.charset != null) return false;
        if (defaultCookies != null ? !defaultCookies.equals(crawlerSet.defaultCookies) : crawlerSet.defaultCookies != null)
            return false;
        if (domain != null ? !domain.equals(crawlerSet.domain) : crawlerSet.domain != null) return false;
        if (headers != null ? !headers.equals(crawlerSet.headers) : crawlerSet.headers != null) return false;
        if (startRequests != null ? !startRequests.equals(crawlerSet.startRequests) : crawlerSet.startRequests != null)
            return false;
        if (userAgent != null ? !userAgent.equals(crawlerSet.userAgent) : crawlerSet.userAgent != null) return false;

        return true;
    }

    @Override
    public int hashCode () {
        int result = domain != null ? domain.hashCode() : 0;
        result = 31 * result + (userAgent != null ? userAgent.hashCode() : 0);
        result = 31 * result + (defaultCookies != null ? defaultCookies.hashCode() : 0);
        result = 31 * result + (charset != null ? charset.hashCode() : 0);
        result = 31 * result + (startRequests != null ? startRequests.hashCode() : 0);
        result = 31 * result + sleepTime;
        result = 31 * result + retryTimes;
        result = 31 * result + cycleRetryTimes;
        result = 31 * result + timeOut;
        result = 31 * result + (acceptStatusCode != null ? acceptStatusCode.hashCode() : 0);
        result = 31 * result + (headers != null ? headers.hashCode() : 0);
        return result;
    }

    @Override
    public String toString () {
        return "CrawlerSet{" +
                "domain='" + domain + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", defaultCookies=" + defaultCookies +
                ", cookies=" + cookies +
                ", charset='" + charset + '\'' +
                ", startRequests=" + startRequests +
                ", sleepTime=" + sleepTime +
                ", retryTimes=" + retryTimes +
                ", cycleRetryTimes=" + cycleRetryTimes +
                ", timeOut=" + timeOut +
                ", retrySleepTime=" + retrySleepTime +
                ", acceptStatusCode=" + acceptStatusCode +
                ", headers=" + headers +
                ", httpProxy=" + httpProxy +
                ", usernamePasswordCredentials=" + usernamePasswordCredentials +
                ", useGzip=" + useGzip +
                '}';
    }

    public UsernamePasswordCredentials getUsernamePasswordCredentials () {
        return usernamePasswordCredentials;
    }

    public void setUsernamePasswordCredentials (UsernamePasswordCredentials usernamePasswordCredentials) {
        this.usernamePasswordCredentials = usernamePasswordCredentials;
    }


}
