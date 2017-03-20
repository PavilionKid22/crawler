package cn.mvncode.webcrawler.Downloadpage;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.HttpContext;

import java.net.URI;

/**
 * 支持post 302跳转策略实现类
 *HttpClient默认跳转：httpClientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
 *上述代码在post/redirect/post这种情况下不会传递原有请求的数据信息。
 * copy by webmagic
 * Created by Pavilion on 2017/3/20.
 */
public class CustomRedirectStrategy extends LaxRedirectStrategy {

    @Override
    public HttpUriRequest getRedirect (HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        URI uri = getLocationURI(request, response, context);
        String method = request.getRequestLine().getMethod();
        if ("post".equalsIgnoreCase(method)) {
            try {
                HttpRequestWrapper httpRequestWrapper = (HttpRequestWrapper) request;
                httpRequestWrapper.setURI(uri);
                httpRequestWrapper.removeHeaders("Content-Length");
                return httpRequestWrapper;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new HttpPost(uri);
        } else {
            return new HttpGet(uri);
        }
    }
}
