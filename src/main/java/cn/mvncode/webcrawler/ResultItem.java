package cn.mvncode.webcrawler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 存储网页解析结果
 * Created by Pavilion on 2017/3/17.
 */
public class ResultItem {

    private Map<String, Object> fields = new LinkedHashMap<String, Object>();

    public <T> ResultItem put (String key, T value) {
        fields.put(key, value);
        return this;
    }

    public <T> T get (String key) {
        Object o = fields.get(key);
        if (o == null) {
            return null;
        }
        return (T) fields.get(key);
    }

    public Map<String, Object> getFields () {
        return fields;
    }
}
