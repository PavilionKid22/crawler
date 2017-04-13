package cn.mvncode.webcrawler.Downloadpage;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.Proxy.Proxy;
import cn.mvncode.webcrawler.Request;
import cn.mvncode.webcrawler.Utils.UrlUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * 下载网页
 * Created by Pavilion on 2017/3/14.
 */
public class DownloadPage extends Downloader {

    private static Logger logger = LoggerFactory.getLogger(DownloadPage.class);

    private Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();

    private static List<String> userAgents = new ArrayList<String>();

    static {
        String path = "D:\\IdeaPro\\crawler\\src\\main\\resources\\userAgent.json";
        String userAgentContent = null;
        try {
            byte[] contentBytes = Files.readAllBytes(Paths.get(path));
            userAgentContent = new String(contentBytes);
        } catch (IOException e) {
//            e.printStackTrace();
            logger.error("read userAgent.json failed");
        }
        JSONObject jsonObject = JSONObject.fromObject(userAgentContent);
        JSONArray jsonArray = jsonObject.getJSONArray("User-Agents");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject tmpJson = JSONObject.fromObject(jsonArray.get(i));
            String userAgent = tmpJson.getString("User-Agent");
            userAgents.add(userAgent);
        }
    }


    /**
     * 获取客户端(beta0.1.1)
     *
     * @param set
     * @return
     */
    private CloseableHttpClient getHttpClient (CrawlerSet set, Proxy proxy) {
        if (set == null) {
            return HttpClientFactory.getClient();
        }
        String domain = set.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = HttpClientFactory.getClient(set, proxy);
                httpClients.put(domain, httpClient);
            }
        }
        return httpClient;
    }

    /**
     * 下载网页并保存(beta0.1.1)
     *
     * @param request
     * @param crawlerSet
     * @param proxy
     * @return
     */
    @Override
    public Page download (Request request, CrawlerSet crawlerSet, Proxy proxy) {

        Page page = new Page();//防止空指针异常
        //获取请求
        HttpUriRequest httpUriRequest = getHttpUriRequest(request, crawlerSet);
        //获取response
        CloseableHttpClient httpClient = getHttpClient(crawlerSet, proxy);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = getResponse(httpClient, httpUriRequest);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return page;
        }

        try {
            page = handleResponse(request, httpResponse, crawlerSet);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return page;
        }

        return page;
    }

    /**
     * 实现HttpUriRequest接口(beta0.1.1)
     *
     * @param request
     * @param crawlerSet
     * @return
     */
    private HttpUriRequest getHttpUriRequest (Request request, CrawlerSet crawlerSet) {
        RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());
        //伪装爬虫
        int index = new Random().nextInt(userAgents.size() - 1 - 0 + 1);
        String tmpUserAgent = userAgents.get(index);
        requestBuilder.addHeader("User_Agent", tmpUserAgent);
        //添加Cookie(有必要时)
        if (!crawlerSet.getDefaultCookies().isEmpty()) {
            for (Map.Entry<String, String> entry : crawlerSet.getDefaultCookies().entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        //提供refer
        if (request.getRefer() != null) {
            requestBuilder.addHeader("Referer", request.getRefer());
        }
        //定制请求规范
        requestBuilder.setConfig(RequestConfig.custom()
                .setConnectTimeout(crawlerSet.getTimeOut() / 2)//连接超时
                .setSocketTimeout(crawlerSet.getTimeOut())//请求获取数据的超时时间response
//                .setConnectionRequestTimeout(crawlerSet.getTimeOut())
                .setCookieSpec(CookieSpecs.STANDARD)//管理cookie规范
                .build());

        HttpUriRequest httpUriRequest = requestBuilder.build();
        return httpUriRequest;
    }

    /**
     * 设置请求信息(beta0.1.0)
     *
     * @param request
     * @return default getMethod
     */
    private RequestBuilder selectRequestMethod (Request request) {
        String method = request.getMethod();
        if (method == null || method.equalsIgnoreCase("GET")) {
            //默认get
            request.setMethod("get");
            return RequestBuilder.get();
        } else if (method.equalsIgnoreCase("POST")) {
            RequestBuilder requestBuilder = RequestBuilder.post();
            //==================
            NameValuePair[] nameValuePairs = (NameValuePair[]) request.getExtra("nameValuePair");
            if (nameValuePairs != null && nameValuePairs.length > 0) {
                requestBuilder.addParameters(nameValuePairs);
            }
            return requestBuilder;
        } else if (method.equalsIgnoreCase("HEAD")) {
            return RequestBuilder.head();
        } else if (method.equalsIgnoreCase("PUT")) {
            return RequestBuilder.put();
        } else if (method.equalsIgnoreCase("DELETE")) {
            return RequestBuilder.delete();
        } else if (method.equalsIgnoreCase("TRACE")) {
            return RequestBuilder.trace();
        }
        throw new IllegalArgumentException("Illegal HTTP Method" + method);
    }

    /**
     * 获取response(stable0.1.0)
     *
     * @param httpUriRequest
     * @return
     */
    private CloseableHttpResponse getResponse (CloseableHttpClient httpClient, HttpUriRequest httpUriRequest) throws IOException {
        CloseableHttpResponse response = httpClient.execute(httpUriRequest);
        return response;
    }

    /**
     * 获取网页编码(stable0.1.1)
     *
     * @param httpResponse
     * @return charset
     */
    private String getHtmlCharset (HttpResponse httpResponse, byte[] contentBytes) throws IOException {
        String charset = null;
        //从header获取charset
        String value = httpResponse.getEntity().getContentType().getValue();
        charset = UrlUtils.getCharset(value);
        if (StringUtils.isNotBlank(charset)) {
            return charset;
        }
        //从meta获取charset
        String content = new String(contentBytes, Charset.defaultCharset().name());
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
     * 获取response消息主体(stable0.1.1)
     *
     * @param httpResponse
     * @param htmlCharset
     * @return
     * @throws IOException
     */
    private String getContent (HttpResponse httpResponse, String htmlCharset) throws IOException {
        byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
        if (htmlCharset == null) {
            String charset = getHtmlCharset(httpResponse, contentBytes);
            if (charset != null) {
                return new String(contentBytes, charset);
            } else {
                return new String(contentBytes);
            }
        } else {
            return new String(contentBytes, htmlCharset);
        }
    }

    /**
     * 保存网页到page(beta0.1.2)
     *
     * @param request
     * @param httpResponse
     * @return
     * @throws IOException
     */
    private Page handleResponse (Request request, HttpResponse httpResponse, CrawlerSet set) throws IOException {

        String charset = set.getCharset();
        //获取content
        String content;
        byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
        if (charset == null) {
            charset = getHtmlCharset(httpResponse, contentBytes);
            if (charset != null) {
                content = new String(contentBytes, charset);
            } else {
                content = new String(contentBytes);
            }
        } else {
            content = new String(contentBytes, charset);
        }

        Page page = new Page();
        page.setCharset(charset);
        page.setRequest(request);
        page.setHttpResponse(httpResponse);
        page.setPlainText(content);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        page.setUrl(request.getUrl());
        page.setCrawlerSet(set);
        page.addTargetUrl(request);//加入当前url进入待处理队列

        //确保连接释放回模拟器池
        if (httpResponse != null) {
            EntityUtils.consume(httpResponse.getEntity());
        }

        return page;
    }

}
