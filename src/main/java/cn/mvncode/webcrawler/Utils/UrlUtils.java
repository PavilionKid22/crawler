package cn.mvncode.webcrawler.Utils;

import cn.mvncode.webcrawler.Request;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Pavilion on 2017/3/14.
 */
public class UrlUtils {


    /**
     * 提取编码方法
     *
     * @param contentType
     * @return charset
     */
    public static String getCharset (String contentType) {
        //编码集样式
        Pattern patternForCharset = Pattern.compile("charset\\s*=\\s*['\"]*([^\\s;'\"]*)");
        //匹配样式
        Matcher matcher = patternForCharset.matcher(contentType);
        if (matcher.find()) {
            String charset = matcher.group(1);//定组
            if (Charset.isSupported(charset)) {//是否匹配
                return charset;
            }
        }
        return null;
    }

    /**
     * 去除协议
     *
     * @param url
     * @return
     */
    public static String removeProtocol (String url) {
        Pattern patternForProtocol = Pattern.compile("[\\w]+://");
        return patternForProtocol.matcher(url).replaceAll("");
    }

    /**
     * 获取domain
     *
     * @param url
     * @return
     */
    public static String getDomain (String url) {
        String domain = removeProtocol(url);
        int i = StringUtils.indexOf(domain, "/", 1);
        if (i > 0) {
            domain = StringUtils.substring(domain, 0, i);
        }
        return domain;
    }

    /**
     * 获取带id的domian(豆瓣，未完成)
     *
     * @param url
     * @return
     */
    public static String getDomainWithID (String url) {
        String domain = getDomain(url);

        return domain;
    }

    /**
     * 除去端口
     *
     * @param domain
     * @return
     */
    public static String removePort (String domain) {
        int portIndex = domain.indexOf(":");
        if (portIndex != -1) {
            return domain.substring(0, portIndex);
        } else {
            return domain;
        }
    }

    /**
     * 返回host
     *
     * @param url
     * @return
     */
    public static String getHost (String url) {
        String host = url;
        int i = StringUtils.ordinalIndexOf(url, "/", 3);
        if (i > 0) {
            host = StringUtils.substring(url, 0, i);
        }
        return host;
    }

    /**
     * 获取子url
     *
     * @param refer
     * @param targetUrls
     * @param key
     * @return
     */
    public static String getSuburl (String refer, Set<Request> targetUrls, String key) {
        StringBuffer targetUrl = new StringBuffer(refer);
        targetUrl.append("/" + key);
        for (Request targetRequest : targetUrls) {
            String tmpUrl = targetRequest.getUrl();
            int i = tmpUrl.indexOf(key, 1);
            if (i > -1) {
                if (StringUtils.substring(tmpUrl, 0, i).equals(refer + "/")) {
//                    System.out.println(tmpUrl);
                    return tmpUrl;
                }
            }
        }
        return null;
    }

    /**
     * 检验ip合法性
     *
     * @param ip
     * @return
     */
    public static boolean checkIP (String ip) {
        Pattern patternForIp = Pattern.compile("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$");
        Matcher matcher = patternForIp.matcher(ip);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    /**
     * 匹配协议
     *
     * @param protocol
     * @return
     */
    public static boolean checkProtocol (String protocol) {
        Pattern patternForProtocol = Pattern.compile("\\bhttp\\b|\\bftp\\b|\\bhttps\\b");
        Matcher matcher = patternForProtocol.matcher(protocol.toLowerCase());
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    /**
     * 提取网页中包含的所有链接(beta0.1.0)
     *
     * @param document
     * @return
     */
    private Set<Request> getUngrapUrl (Document document) {

        Set<Request> ungrapUrl = new HashSet<Request>();//简单去重

        Elements links = document.select("a[href]");//带有href属性的a元素
        Elements medias = document.select("[src]");//带有src属性的所有元素
        Elements imports = document.select("link[href]");//带有href属性的link元素

        for (Element link : links) {
            if (!link.attr("abs:href").isEmpty()) {
                ungrapUrl.add(new Request(link.attr("abs:href")));
            }
        }

        for (Element media : medias) {
            if (!media.attr("abs:src").isEmpty()) {
                ungrapUrl.add(new Request(media.attr("abs:src")));
            }
        }

        for (Element element : imports) {
            if (!element.attr("abs:href").isEmpty()) {
                ungrapUrl.add(new Request(element.attr("abs:href")));
            }
        }

        return ungrapUrl;
    }
}
