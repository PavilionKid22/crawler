package cn.mvncode.webcrawler.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Pavilion on 2017/3/25.
 */
public class DateUtil {

    /**
     * 打印当前时间
     *
     * @return
     */
    public static String printTimeNow () {
        return "\t" + timeNow();
    }

    /**
     * 返回当前时间
     *
     * @return
     */
    public static String timeNow () {
        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
        return dateFormat.format(new Date());
    }

}
