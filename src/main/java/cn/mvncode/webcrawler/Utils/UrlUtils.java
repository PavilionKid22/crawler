package cn.mvncode.webcrawler.Utils;

import cn.mvncode.webcrawler.Request;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Pavilion on 2017/3/14.
 */
public class UrlUtils {


    /**
     * 规范化url
     * 未完成
     *
     * @param url
     * @param refer
     * @return
     */
    public static String canonocalizeUrl (String url, String refer) {
        URL base;
        try {
            try {
                base = new URL(refer);
            } catch (MalformedURLException e) {
                URL abs = new URL(refer);
                return abs.toExternalForm();
            }
            if (url.startsWith("?"))
                url = base.getPath() + url;
            URL abs = new URL(base, url);
            return url.replace(" ", "%20");
        } catch (MalformedURLException e) {
            return "";
        }
    }

    /**
     * 从Request获取url
     *
     * @param requests
     * @return
     */
    public static List<String> convertToUrls (Collection<Request> requests) {
        List<String> urlList = new ArrayList<String>(requests.size());
        for (Request request : requests) {
            urlList.add(request.getUrl());
        }
        return urlList;
    }
}
