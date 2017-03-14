package cn.mvncode.webcrawler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

    private String url;

    private String method;

    /**
     * 存储额外的信息
     */
    private Map<String, Object> extras;

    public Request () {

    }

    public Request (String url) {
        this.url = url;
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

    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Request request = (Request) obj;
        if (!url.equals(request.url)) return false;

        return true;
    }

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
