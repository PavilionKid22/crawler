package cn.mvncode.webcrawler.downloadpage;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * 获取客户端
 * <p>
 * Created by Pavilion on 2017/3/14.
 */
public class httpClientFactory {

    private static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

    static {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
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
     * @return HttpClient
     */
    public static CloseableHttpClient getClient () {
        return HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).build();
    }


}
