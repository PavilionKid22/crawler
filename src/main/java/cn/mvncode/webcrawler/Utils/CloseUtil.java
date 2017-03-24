package cn.mvncode.webcrawler.Utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Pavilion on 2017/3/22.
 */
public class CloseUtil {

    /**
     * 销毁对象
     *
     * @param object
     */
    public static void destroyEach (Object object) {
        if (object instanceof Closeable) {//检查是否属于需要关闭类的实例
            try {
                ((Closeable) object).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
