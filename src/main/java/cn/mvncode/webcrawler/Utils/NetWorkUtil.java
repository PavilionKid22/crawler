package cn.mvncode.webcrawler.Utils;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Pavilion on 2017/4/15.
 */
public class NetWorkUtil {

    private static Logger logger = LoggerFactory.getLogger(NetWorkUtil.class.getName());

    /**
     * 判断网络连接状态
     *
     * @return
     */
    public static boolean isConnect () {
        boolean connect = false;
        Runtime runtime = Runtime.getRuntime();//获取控制台
        Process process;
        String content = null;
        try {
            process = runtime.exec("ping " + "www.baidu.com");
            InputStream inputStream = process.getInputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            content = new String(bytes, "GBK");
            inputStream.close();
            if(content!=null) {
                if (content.indexOf("TTL") != -1) connect = true;
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return connect;
    }

}
