package cn.mvncode.webcrawler.Downloadpage;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 利用phantomjs下载ajax生成的页面
 * Created by Pavilion on 2017/3/24.
 */
public class DownloadAjaxPage extends Downloader {

    /**
     * 获取ajax网页内容
     *
     * @param request
     * @return
     * @throws IOException
     */
    private String getAjaxcontent (Request request) throws IOException {
        String url = request.getUrl();
        //管道runtime调用cmd
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec("phantomjs D:\\IdeaPro\\crawler\\src\\main\\resources\\getAjaxPage.js " + url);//cmd命令
        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer sbf = new StringBuffer();
        String tmp = "";
        while ((tmp = br.readLine()) != null) {
            sbf.append(tmp);
        }

        return sbf.toString();
    }

    /**
     * 获取charset
     *
     * @param content
     * @return
     */
    private String getCharset (String content) {
        String charset = null;
        //从meta获取charset
        if (StringUtils.isNotEmpty(content)) {
            Document document = Jsoup.parse(content);
            Elements metas = document.select("meta");
            for (Element meta : metas) {
                String metaContent = meta.attr("content");
                String metaCharset = meta.attr("charset");
                //<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                if (metaContent.indexOf("charset") != -1) {
                    metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
                    charset = metaContent.split("=")[1];
                    break;
                }
                //<meta charset="UTF-8" />
                else if (StringUtils.isNotEmpty(metaCharset)) {
                    charset = metaCharset;
                    break;
                }
            }
        }
        return charset;
    }

    /**
     * download
     *
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    public Page download (Request request, CrawlerSet set, Proxy proxy) {

        Page page = new Page();
        String content = null;
        try {
            content = getAjaxcontent(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String charset = getCharset(content);

        page.setRequest(request);
        page.setUrl(request.getUrl());
        page.setCrawlerSet(CrawlerSet.setDefault());
        page.setPlainText(content);
        page.setCharset(charset);

        return page;
    }

}
