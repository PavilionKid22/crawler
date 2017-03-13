import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.*;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Pavilion on 2017/3/13.
 */
public class FirstPageCrawler {

    private static PoolingHttpClientConnectionManager cm;

    static {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainsf)
                .register("https", sslsf)
                .build();
        cm = new PoolingHttpClientConnectionManager(registry);
        //路由基础的连接
        cm.setDefaultMaxPerRoute(100);
        //最大连接数
        cm.setMaxTotal(10);
    }

    /**
     * @return HttpClient
     */
    private CloseableHttpClient getClient () {
        return HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * @return requestConfig
     */
    private RequestConfig getRequestConfig () {
        return RequestConfig.DEFAULT;
    }

    /**
     * @param url
     * @return method
     */
    private HttpGet createGetMethod (String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(getRequestConfig());
        return httpGet;
    }


    /**
     * 获取网页编码
     *
     * @param response
     * @return charset
     */
    private String getEncoding (CloseableHttpResponse response) {
        String charset = null;
        //从header获取charset
        Pattern patternForCharset = Pattern.compile("charset\\s*=\\s*['\"]*([^\\s;'\"]*)");
        String value = response.getEntity().getContentType().getValue();
        System.out.println(value);
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
     * execute request
     *
     * @param url
     */
    public void execute (String url) {
        CloseableHttpClient httpClient = getClient();
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(createGetMethod(url));
            String charset = getEncoding(response);
            System.out.println(charset);
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String lineString = null;
            while ((lineString = bufferedReader.readLine()) != null) {
                stringBuffer.append(lineString);
                stringBuffer.append("\n");
            }
            System.out.println(stringBuffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return new FirstPageCrawler
     */
    public static FirstPageCrawler me () {
        return new FirstPageCrawler();
    }

}
