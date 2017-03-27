package cn.mvncode.webcrawler.Proxy;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Downloadpage.DownloadAjaxPage;
import cn.mvncode.webcrawler.Downloadpage.DownloadPage;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.Utils.CloseUtil;
import cn.mvncode.webcrawler.Utils.DateUtil;
import cn.mvncode.webcrawler.Utils.UrlUtils;
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
import java.util.concurrent.*;

/**
 * Created by Pavilion on 2017/3/20.
 */
public class SimpleProxyPool {

    private DelayQueue<Proxy> proxies = new DelayQueue<Proxy>();//代理池
    private Proxy currentProxy;//正在使用的代理

    private DownloadPage downloadPage = new DownloadPage();//抓取代理模拟器

    private DownloadPage testDownload = new DownloadPage();//测试模拟器
    private Page page = new Page();
    private Page testPage = null;

    private String testUrl;
    private String file;
    private String charset;

    public SimpleProxyPool () {
        file = "D:\\IdeaPro\\crawler\\src\\main\\java\\cn\\mvncode\\webcrawler" +
                "\\Proxy\\ProxyIpPool.txt";
        charset = Charset.defaultCharset().name();
        testUrl = "http://www.baidu.com";
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
            System.out.println("recovery proxy:" + currentProxy.getHttpHost().getHostName() + DateUtil.timeNow());//tttttttttttttt
            currentProxy.resetInterval();
            proxies.offer(currentProxy);
        }
        currentProxy = null;
        //提取代理
        Proxy tmpProxy = proxies.poll();
        if (tmpProxy == null) {
            return null;
        }
        if(testProxy(tmpProxy)){
            currentProxy = tmpProxy;//标记
            System.out.println("get proxy succeed!\t" + currentProxy.getHttpHost().getHostName() +
                    "\tleft = " + proxies.size() + DateUtil.timeNow());//tttttttttttttttttttttt
        }
        return currentProxy;
    }

    /**
     * 抓取代理注入池中
     *
     * @throws IOException
     */
    public void getProxyToPool () throws IOException {
//        System.out.println("获取代理..." + DateUtil.timeNow());//tttt
        String url = "http://www.xdaili.cn/freeproxy.html";
        getWebAPI(url);
        parsePage();
    }

    /**
     * 测试代理可用性
     *
     * @param proxy
     * @return
     */
    public boolean testProxy (Proxy proxy) throws IOException {
        ExecutorService service = Executors.newSingleThreadScheduledExecutor();//测试线程池
        //测试任务
        Callable<Boolean> call = new Callable<Boolean>() {
            @Override
            public Boolean call () throws Exception {
                testPage = testDownload.download(new Request(testUrl),
                        CrawlerSet.setDefault().setDomain(UrlUtils.getDomain(testUrl)), proxy);
                if (testPage == null) {
                    return false;
                } else {
                    if (testPage.getStatusCode() < 300 && testPage.getStatusCode() >= 200) {
                        return true;
                    }
                }
                return false;
            }
        };
        //超时策略
        Future<Boolean> future = service.submit(call);
        boolean flag = false;
        try {
            flag = future.get(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.err.println(proxy.getHttpHost() + "\ttest request timeout" + DateUtil.timeNow());
//            e.printStackTrace();
            service.shutdown();
            return false;
        }
        service.shutdown();
        return flag;
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
//        CrawlerSet set = CrawlerSet.setDefault();
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
    public void parsePage () throws IOException {

        byte[] contentBytes = Files.readAllBytes(Paths.get(file));
        String content = new String(contentBytes, charset);

        //转化格式
        Document document = Jsoup.parse(content);
        //解析文件
        Elements elements = document.select("div#table1");
        Elements tbody = elements.select("tbody#target").select("tr");
        for (Element element : tbody) {
            Elements texts = element.select("td");
            StringBuffer stringBuffer = new StringBuffer();
            String ip = null;
            String port = null;
            String httpType = null;
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
                if (testProxy(proxy)) {//测试代理
                    proxies.offer(new Proxy(httpHost));
                }
            }
        }
        System.out.println("pool size：" + proxies.size() + DateUtil.timeNow());//ttttttttttttttttt

    }


    /**
     * 关闭模拟器
     */
    public void close () {
        CloseUtil.destroyEach(downloadPage);
        CloseUtil.destroyEach(testDownload);
    }

}
