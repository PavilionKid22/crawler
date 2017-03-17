package cn.mvncode.webcrawler.downloadpage;

import cn.mvncode.webcrawler.CrawlerSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
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
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.net.ProxySelector;
import java.util.Map;

/**
 * 获取客户端
 * <p>
 * Created by Pavilion on 2017/3/14.
 */
public class httpClientFactory {

    private static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

    /**
     * 初始化连接池
     */
    static {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();//直连
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();//安全连接
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainsf)
                .register("https", sslsf)
                .build();
        poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(registry);
        //路由基础的连接
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(20);
        //最大连接数
        poolingHttpClientConnectionManager.setMaxTotal(800);
    }

    /**
     * 获取默认客户端
     *
     * @return
     */
    public static CloseableHttpClient getClient () {
        return HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).build();
    }

    /**
     * 获得客户端
     *
     * @return HttpClient
     */
    public static CloseableHttpClient getClient (CrawlerSet crawlerSet) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        //使用连接池管理器
        httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);

        //身份认证
        CredentialsProvider credentialsProvider = null;
        if (crawlerSet != null && crawlerSet.getUsernamePasswordCredentials() != null) {
            credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                    new AuthScope(crawlerSet.getHttpProxy()),//可以访问的范围
                    crawlerSet.getUsernamePasswordCredentials()//用户名和密码
            );
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        }

        //解决post/redirect/post 302跳转问题
//        httpClientBuilder.setRedirectStrategy();

        //Socket设置
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(5000).setSoKeepAlive(true).setTcpNoDelay(true).build();
        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        poolingHttpClientConnectionManager.setDefaultSocketConfig(socketConfig);

        //代理设置(使用JRE代理选择器来获取代理配置)
        SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(
                ProxySelector.getDefault()
        );
        httpClientBuilder.setRoutePlanner(routePlanner);

        //Cookie持久化设置
        generateCookie(httpClientBuilder, crawlerSet);

        return httpClientBuilder.build();
    }

    /**
     * 生成Cookie持久化
     *
     * @param httpClientBuilder
     */
    private static void generateCookie (HttpClientBuilder httpClientBuilder, CrawlerSet crawlerSet) {
        CookieStore cookieStore = new BasicCookieStore();//持久化容器
        for (Map.Entry<String, String> cookieEntry : crawlerSet.getDefaultCookies().entrySet()) {
            BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
            cookie.setDomain(crawlerSet.getDomain());
            cookieStore.addCookie(cookie);
        }
        for (Map.Entry<String, Map<String, String>> domainEntry : crawlerSet.getCookies().entrySet()) {
            for (Map.Entry<String, String> cookieEntry : domainEntry.getValue().entrySet()) {
                BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                cookie.setDomain(domainEntry.getKey());
                cookieStore.addCookie(cookie);
            }
        }
        httpClientBuilder.setDefaultCookieStore(cookieStore);
    }


}
