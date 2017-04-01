package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.Downloader;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.ResultItem;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pavilion on 2017/3/28.
 */
public class PageListHandler extends PageResponseHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取结果
     *
     * @param seek
     * @param set
     * @param proxy
     * @param downloader
     * @return
     * @throws IOException
     */
    @Override
    public ResultItem getHandler (Request seek, CrawlerSet set, Proxy proxy, Downloader downloader) throws IOException {
        this.proxy = proxy;
        handleResponse(seek, set, downloader);
        return resultItem;
    }

    /**
     * 解析网页
     *
     * @param seek
     * @param set
     * @param downloader
     * @throws IOException
     */
    @Override
    public void handleResponse (Request seek, CrawlerSet set, Downloader downloader) throws IOException {

        Set<JSONObject> Jsons = getJSONs(seek, set, downloader);
        for (JSONObject jsonObject : Jsons) {
            JSONArray array = jsonObject.getJSONArray("subjects");//提取数组
            for (int i = 0; i < array.size(); i++) {
                JSONObject tmpJson = JSONObject.fromObject(array.getString(i));//转化为json
                String title = tmpJson.getString("title");
                String url = tmpJson.getString("url");
                resultItem.put(title, url);
            }
        }

    }

    /**
     * 获取JSON对象
     *
     * @param targetUrl
     * @param set
     * @param downloader
     * @return
     */
    public Set<JSONObject> getJSONs (Request targetUrl, CrawlerSet set, Downloader downloader) {

        Set<JSONObject> Jsons = new HashSet<JSONObject>();
        String url;
        String part1 = "https://movie.douban.com/j/search_subjects?type=movie&tag=";
        String part2 = getTag(targetUrl.getUrl());
        String part3 = "&sort=recommend&page_limit=20&page_start=";
        int count = 0;
        Page tmpPage;
        String jsonNull = "{\"subjects\":[]}";

        while (true) {
            url = part1 + part2 + part3 + Integer.toString(count);//获取目标url
            Request request = new Request(url);
            tmpPage = downloader.download(request, set, proxy);
            if (tmpPage.getStatusCode() >= 300) {
                logger.error("failed get url: " + url + "\tstatusCode: " + tmpPage.getStatusCode());
                count += 20;
                continue;
            }
            if (tmpPage.getPlainText().equals(jsonNull)) {
                break;
            }
            //将Json字符串转化为Json对象
            JSONObject tmpJson = JSONObject.fromObject(tmpPage.getPlainText());
            Jsons.add(tmpJson);
            count += 20;
            try {
                TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000 - 500 + 1) + 500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return Jsons;
    }

    /**
     * 获取tag标签
     *
     * @param targetUrl
     * @return
     */
    public String getTag (String targetUrl) {

        int start = StringUtils.indexOf(targetUrl, "tag", 1);
        int end = StringUtils.indexOf(targetUrl, "sort", start);
        String tag = targetUrl.substring(start + 4, end - 1);

        return tag;
    }


}
