package cn.mvncode.webcrawler.Proxy;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.DownloadAjaxPage;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.Utils.CloseUtil;
import cn.mvncode.webcrawler.Utils.UrlUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.concurrent.DelayQueue;

/**
 * Created by Pavilion on 2017/3/20.
 */
public class SimpleProxyPool {

    private DelayQueue<Proxy> proxies = new DelayQueue<Proxy>();//代理池
    private Proxy currentProxy;//正在使用的代理

    private String file = "D:\\IdeaPro\\crawler\\src\\main\\java\\cn\\mvncode\\webcrawler" +
            "\\Proxy\\ProxyIpPool.txt";
    private String charset = Charset.defaultCharset().name();

    private DownloadPage downloadPage = new DownloadPage();//抓取代理模拟器
    private DownloadPage testDownload = new DownloadPage();//测试模拟器
    private Page page = new Page();

    private Page testPage = new Page();

    private String testUrl = "http://www.baidu.com";


    public Proxy getCurrentProxy () {
        return currentProxy;
    }

    /**
     * 从代理池中提取代理fifo
     *
     * @return
     */
    public Proxy getProxy () throws IOException {
        if (proxies.isEmpty() || proxies.size() < 1) {
            return null;
        }
        //回收代理
        if (currentProxy != null && testProxy(currentProxy)) {
            proxies.offer(currentProxy);
        } else {
            currentProxy = null;
        }
        //提取代理
        Proxy proxy = proxies.poll();
        if (proxy == null) {
            return null;
        }
        currentProxy = proxy;//标记
        return proxy;
    }

    /**
     * 抓取代理注入池中
     *
     * @throws IOException
     */
    public void getProxyToPool () throws IOException {
        String url = "http://www.xdaili.cn/freeproxy.html";
        getWebAPI(url);
        parseJson();
    }

    /**
     * 测试代理可用性
     *
     * @param proxy
     * @return
     */
    public boolean testProxy (Proxy proxy) throws IOException {
        testPage = testDownload.download(new Request(testUrl), CrawlerSet.setByDefault(), proxy);
        if (page.getStatusCode() < 300 && page.getStatusCode() >= 200) {
            return true;
        }
        return false;
    }

    /**
     * 获取数据写入文件（定制）
     *
     * @param url
     * @return
     */
    public void getWebAPI (String url) throws IOException {

        if (url.equals("")) {
            throw new IOException("url failed");
        }
//        Request request = new Request(url);
//        CrawlerSet set = CrawlerSet.setByDefault();
//        page = downloadPage.download(request, set, null);
        DownloadAjaxPage downloadAjaxPage = new DownloadAjaxPage();
        page = downloadAjaxPage.getPage(new Request(url));

        //将返回内容写入文件
        charset = page.getCharset();
        byte[] contentBytes = page.getPlainText().getBytes(charset);
        Files.write(Paths.get(file), contentBytes, StandardOpenOption.CREATE);

    }

    /**
     * 解析网页（定制）
     *
     * @return
     */
    public void parseJson () throws IOException {

        byte[] contentBytes = Files.readAllBytes(Paths.get(file));
        String content = new String(contentBytes, charset);

        //转化格式
        Document document = Jsoup.parse(content);
        //解析文件
        StringBuffer stringBuffer = new StringBuffer();
        String ip = null;
        String port = null;
        String httpType = null;
        Elements elements = document.select("div#table1");
        Elements tbody = elements.select("tbody#target").select("tr");
        System.out.println(tbody);
        for (Element element : tbody) {
            Elements texts = element.select("td");
            for (Element text : texts) {
                stringBuffer.append(text.text() + "/");
            }
            String context = stringBuffer.toString();
            String[] temp = context.split("/");
            ip = temp[0];
            port = temp[1];
            httpType = temp[3];
            if (UrlUtils.checkIP(ip) && UrlUtils.checkProtocol(httpType)) {
                //加入队列
                HttpHost httpHost = new HttpHost(ip, Integer.parseInt(port), httpType.toLowerCase());
                Proxy proxy = new Proxy(httpHost);
                if (testProxy(proxy)) {
                    proxies.offer(new Proxy(httpHost));
                }
            }
            System.out.println(httpType + "://" + ip + ":" + port);
        }

    }


    /**
     * 关闭模拟器
     */
    public void close () {
        CloseUtil.destroyEach(downloadPage);
        CloseUtil.destroyEach(testDownload);
    }

}
