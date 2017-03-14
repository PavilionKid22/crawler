package cn.mvncode.webcrawler.Utils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Pavilion on 2017/3/14.
 */
public class UrlUtils {


    /**
     * 规范化url
     * 未完成
     * @param url
     * @param refer
     * @return
     */
    public static String canonocalizeUrl(String url,String refer){
        URL base;
        try {
            try {
                base = new URL(refer);
            }catch (MalformedURLException e){
                URL abs = new URL(refer);
                return abs.toExternalForm();
            }
            if(url.startsWith("?"))
                url=base.getPath()+url;
            URL abs = new URL(base,url);
            return url.replace(" ","%20");
        } catch (MalformedURLException e) {
            return "";
        }
    }








}
