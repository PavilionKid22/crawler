package cn.mvncode.webcrawler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 对URL的封装，一个url对应一个request
 * <p>
 * Created by Pavilion on 2017/3/14.
 */
public class Request implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 1L;

    public static final String CYCLE_TRIED_TIMES = "_cycle_tried_times";
    public static final String STATUS_CODE = "statusCode";
    public static final String PROXY = "proxy";

    private String url;

    private String method;

    /**
     * 存储额外的信息
     */
    private Map<String, Object> extras;

    /**
     * 存储头信息
     * 伪装请求
     */
    private static Map<String, String> headers = new HashMap<String, String>();

    static {
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate, sdch");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8");
        headers.put("User_Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
    }

    public void setUrl (String url) {
        this.url = url;
    }

    public Request () {
        url = null;
        method = null;
    }

    public Request (String url) {
        this.url = url;
    }

    public Map<String, String> getHeaders () {
        return headers;
    }

    public void putHeaders (String key, String value) {
        headers.put(key, value);
    }

    public Object getExtra (String key) {
        if (extras == null) {
            return null;
        }
        return extras.get(key);
    }

    public Request putExtras (String key, Object value) {
        if (extras == null) {
            extras = new HashMap<String, Object>();
        }
        extras.put(key, value);
        return this;
    }

//    @Override
//    public boolean equals (Object obj) {
//        if (this == obj) return true;
//        if (obj == null || getClass() != obj.getClass()) return false;
//
//        Request request = (Request) obj;
//        if (!url.equals(request.getUrl())) return false;
//
//        return true;
//    }

    @Override
    public int hashCode () {
        return url.hashCode();
    }

    public Map<String, Object> getExtras () {
        return extras;
    }

    public void setExtras (Map<String, Object> extras) {
        this.extras = extras;
    }

    public void setUrl () {
        this.url = url;
    }

    public String getUrl () {
        return url;
    }

    public String getMethod () {
        return method;
    }

    public void setMethod (String method) {
        this.method = method;
    }

    @Override
    public String toString () {
        return "Request{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", extras=" + extras +
                '}';
    }

}
