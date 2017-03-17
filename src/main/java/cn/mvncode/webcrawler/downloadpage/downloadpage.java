package cn.mvncode.webcrawler.downloadpage;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.Request;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Pavilion on 2017/3/14.
 */
public class downloadpage {

    private static final String DefaultCharSet = "utf-8";

    /**
     * 下载网页并保存
     *
     * @param request
     */
    public Page download (Request request, CrawlerSet crawlerSet) {

        //获取客户端
        CloseableHttpClient httpClient = httpClientFactory.getClient(crawlerSet);
        //获取请求
        HttpUriRequest httpUriRequest = getHttpUriRequest(request, crawlerSet);
        //获取response
        CloseableHttpResponse httpResponse = null;
        httpResponse = getResponse(httpClient, httpUriRequest);

        /*    测试    */
        System.out.println("*General");
        System.out.println("Request URL: "+httpUriRequest.getURI());
        //获取状态码
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        request.putExtras(Request.STATUS_CODE, statusCode);
        System.out.println("Status Code: "+statusCode);
        System.out.println("");

        System.out.println("*Response Headers");
        Header[] responseHeaders = httpResponse.getAllHeaders();
        for (Header header:responseHeaders) {
            System.out.println(header);
        }
        System.out.println("");

        System.out.println("*Request Headers");
        Header[] headers1 = httpUriRequest.getAllHeaders();
        for (Header header : headers1) {
            System.out.println(header);
        }
        System.out.println("");

//        System.out.println("Priview");
//        try {
//            String content = getContent(httpResponse);
//            System.out.println(content);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Page page = new Page();
        page.setRequest(request);
        page.setHttpResponse(httpResponse);

        return page;

    }


    /**
     * 请求基本设置
     *
     * @return requestConfig
     */
    private RequestConfig getRequestConfig (CrawlerSet crawlerSet) {
        return RequestConfig.custom()
                .setConnectTimeout(crawlerSet.getTimeOut())//连接超时
                .setSocketTimeout(crawlerSet.getTimeOut())//请求获取数据的超时时间response
                .setConnectionRequestTimeout(crawlerSet.getTimeOut())
                .setExpectContinueEnabled(true)
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                .setCookieSpec(CookieSpecs.DEFAULT)//管理cookie规范
                .build();
    }

    /**
     * 实现HttpUriRequest接口
     *
     * @param request
     * @return
     */
    private HttpUriRequest getHttpUriRequest (Request request, CrawlerSet crawlerSet) {
        RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());
        for (Map.Entry<String, String> headerEntry : request.getHeaders().entrySet()) {
            requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
        }
        requestBuilder.setConfig(getRequestConfig(crawlerSet));
        HttpUriRequest httpUriRequest = requestBuilder.build();
        return httpUriRequest;
    }

    /**
     * 设置请求信息
     *
     * @param request
     * @return default getMethod
     */
    private RequestBuilder selectRequestMethod (Request request) {
        String method = request.getMethod();
        if (method == null || method.equalsIgnoreCase("GET")) {
            //默认get
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
     * 获取网页编码
     *
     * @param httpResponse
     * @return charset
     */
    private String getHtmlCharset (HttpResponse httpResponse) {
        String charset = null;
        //从header获取charset
        Pattern patternForCharset = Pattern.compile("charset\\s*=\\s*['\"]*([^\\s;'\"]*)");
        String value = httpResponse.getEntity().getContentType().getValue();
//        System.out.println(value);
        Matcher matcher = patternForCharset.matcher(value);
        if (matcher.find()) {
            charset = matcher.group(1);
            if (Charset.isSupported(charset)) {//判断是否为编码格式
                return charset;
            }
        }
        //从meta获取charset


        return charset;
    }

    /**
     * 获取response
     *
     * @param httpUriRequest
     * @return
     */
    private CloseableHttpResponse getResponse (CloseableHttpClient httpClient, HttpUriRequest httpUriRequest) {
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpUriRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 获取response消息主体
     *
     * @param httpResponse
     * @return
     * @throws IOException
     */
    private String getContent (CloseableHttpResponse httpResponse) throws IOException {
        byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
//        /*  test  */
//        String string = new String(contentBytes);
//        System.out.println(string);
//        /*  test  */
        //确保连接关闭
        if (httpResponse != null) {
            EntityUtils.consume(httpResponse.getEntity());
        }
        String charset = getHtmlCharset(httpResponse);
        if (charset != null) {
            return new String(contentBytes, charset);
        } else {
            return new String(contentBytes, DefaultCharSet);
        }
    }


}
