package cn.mvncode.webcrawler.Downloadpage;

import cn.mvncode.webcrawler.CrawlerSet;
import cn.mvncode.webcrawler.Proxy.Proxy;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
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
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.cookie.BasicClientCookie;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.ProxySelector;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * 获取客户端
 * <p>
 * Created by Pavilion on 2017/3/14.
 */
public class HttpClientFactory {

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
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(100);
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

//        //身份认证
//        CredentialsProvider credentialsProvider = null;
//        if (crawlerSet != null && crawlerSet.getUsernamePasswordCredentials() != null) {
//            credentialsProvider = new BasicCredentialsProvider();
//            credentialsProvider.setCredentials(
//                    new AuthScope(crawlerSet.getHttpProxy()),//可以访问的范围
//                    crawlerSet.getUsernamePasswordCredentials()//用户名和密码
//            );
//            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
//        }

        //解决post/redirect/post 302跳转问题
        //HttpClient自动处理所有类型的重定向,通过POST和PUT请求的303 redirect会被转换成GET请求
        httpClientBuilder.setRedirectStrategy(new CustomRedirectStrategy());

        //Socket设置
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(5000).setSoKeepAlive(true).setTcpNoDelay(true).build();
        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        poolingHttpClientConnectionManager.setDefaultSocketConfig(socketConfig);

        //错误恢复机制
        if (crawlerSet != null) {
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(crawlerSet.getRetryTimes(), true));
        }

        //Cookie策略定制
//        generateCookie(httpClientBuilder, crawlerSet);

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
