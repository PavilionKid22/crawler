package cn.mvncode.webcrawler.Downloadpage;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Proxy.Proxy;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 获取客户端
 * <p>
 * Created by Pavilion on 2017/3/14.
 */
public class HttpClientFactory {

    private static Logger logger = LoggerFactory.getLogger(HttpClientFactory.class.getName());

    private static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

    /**
     * 初始化连接池
     */
    static {
        // 设置协议http和https对应的处理socket链接工厂的对象
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.INSTANCE;//直连
        LayeredConnectionSocketFactory sslsf = buildSSLConnectionSocketFactory();//安全连接
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainsf)
                .register("https", sslsf)
                .build();
        poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(registry);
        //路由基础的连接
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(200);
        //设置整个连接池最大连接数
        poolingHttpClientConnectionManager.setMaxTotal(400);
    }

    /**
     * 配置ssl(stale0.1.0)
     *
     * @return
     */
    private static SSLConnectionSocketFactory buildSSLConnectionSocketFactory () {
        try {
            //优先绕过安全证书
            return new SSLConnectionSocketFactory(createIgnoreVerifySSL());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return SSLConnectionSocketFactory.getSocketFactory();
    }

    /**
     * SSL绕过验证(stable0.1.0)
     *
     * @return
     */
    private static SSLContext createIgnoreVerifySSL () throws NoSuchAlgorithmException, KeyManagementException {
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted (X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted (X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers () {
                return null;
            }
        };
        SSLContext sc = SSLContext.getInstance("SSLv3");
        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    /**
     * 获取默认客户端(stable0.1.0)
     *
     * @return
     */
    public static CloseableHttpClient getClient () {
        return HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).build();
    }

    /**
     * 获得客户端(beta0.1.0)
     *
     * @return HttpClient
     */
    public static CloseableHttpClient getClient (CrawlerSet crawlerSet, Proxy proxy) {

        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        //设置代理ip
        if (proxy != null) {
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy.getHttpHost());
            httpClientBuilder.setRoutePlanner(routePlanner);
        }

        //使用连接池管理器
        httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);

        //解决post/redirect/post 302跳转问题
        //HttpClient自动处理所有类型的重定向,通过POST和PUT请求的303 redirect会被转换成GET请求
        httpClientBuilder.setRedirectStrategy(new CustomRedirectStrategy());

        //Socket设置
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(CrawlerSet.set().getTimeOut())
                .setSoKeepAlive(true)
                .setTcpNoDelay(true).build();
        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        poolingHttpClientConnectionManager.setDefaultSocketConfig(socketConfig);

        //错误恢复机制
        if (crawlerSet != null) {
            HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
                @Override
                public boolean retryRequest (IOException e, int i, HttpContext httpContext) {
                    if (i >= crawlerSet.getRetryTimes()) {
                        // 设定重试次数
                        return false;
                    }
                    if (e instanceof InterruptedIOException) {
                        // Timeout
                        logger.error("request timeout");
                        return false;
                    }
                    if (e instanceof UnknownHostException) {
                        // Unknown host
                        logger.error("request unknown host");
                        return false;
                    }
                    if (e instanceof ConnectException) {
                        // Connection refused
                        logger.error("request connection refused");
                        return false;
                    }
                    if (e instanceof SSLException) {
                        // SSL handshake exception
                        logger.error("request SSL handshake exception");
                        return false;
                    }
                    HttpClientContext clientContext = HttpClientContext.adapt(httpContext);
                    HttpRequest request = clientContext.getRequest();
                    boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                    if (idempotent) {
                        // Retry if the request is considered idempotent
                        return true;
                    }
                    return false;
                }
            };
            httpClientBuilder.setRetryHandler(retryHandler);
        }


        return httpClientBuilder.build();
    }

    /**
     * 定制cookie策略(beta0.1.0)
     *
     * @param httpClientBuilder
     */
    private static void generateCookie (HttpClientBuilder httpClientBuilder, CrawlerSet crawlerSet) {
        CookieStore cookieStore = new BasicCookieStore();//持久化容器
        //...
        httpClientBuilder.setDefaultCookieStore(cookieStore);
    }


}
