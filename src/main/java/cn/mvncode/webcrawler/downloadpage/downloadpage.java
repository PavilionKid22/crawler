package cn.mvncode.webcrawler.downloadpage;

import cn.mvncode.webcrawler.Request;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Pavilion on 2017/3/14.
 */
public class downloadpage {

    /**
     * @return requestConfig
     */
    private RequestConfig getRequestConfig () {
        return RequestConfig.DEFAULT;
    }

    /**
     * 实现HttpUriRequest接口
     *
     * @param request
     * @return
     */
    private HttpUriRequest getHttpUriRequest (Request request) {
        RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());
        requestBuilder.setConfig(getRequestConfig());
        return requestBuilder.build();
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
     * @param request
     * @return
     */
    private CloseableHttpResponse getResponse (Request request) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = httpClientFactory.getClient();
        try {
            response = httpClient.execute(getHttpUriRequest(request));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 获取response消息主体
     * @param request
     * @return
     * @throws IOException
     */
    public String getContent (Request request) throws IOException {
        CloseableHttpResponse httpResponse = getResponse(request);
        byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
//        String string = new String(contentBytes);
//        System.out.println(string);
        //关闭客户端
        httpResponse.close();
        String charset = getHtmlCharset(httpResponse);
        if (charset != null) {
            return new String(contentBytes, charset);
        } else {
            return new String(contentBytes);
        }
    }


}
