package cn.mvncode.webcrawler.Utils;

import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Pavilion on 2017/4/9.
 */
public class DataBaseUtilTest {
    @Test
    public void query () throws Exception {

//        String tableName = "movies";
//        String target = "UID";
//        String offer = "Title";
//        String data = "夜色人生";
//
//        System.out.println(DataBaseUtil.queryString(tableName, target, offer, data));

        String baseName = "moviebase";
        String tableName = "movies";
        String target1 = "Title";
        String target2 = "Url";
//        System.out.println(DataBaseUtil.queryTableSize(tableName, target));
//        List<String> tmpList = DataBaseUtil.getList("moviebase", tableName, target);
//        System.out.println(tmpList.size());
//        for (int i = 0; i < tmpList.size(); i++) {
//            System.out.println(tmpList.get(i));
//        }
//        Map<String, String> tmp = DataBaseUtil.getUrlList("moviebase", tableName, target1, target2);
//        for (Map.Entry<String, String> entry : tmp.entrySet()) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
//        }
        Date[] list = DataBaseUtil.getTableStatus(baseName, tableName);
        Date createTime = list[0];
        Date updateTime = list[1];
        Date nowTime = new Date();
        long t1 = createTime.getTime();
        long t2 = nowTime.getTime();
        long t3 = (t2 - t1) / (1000 * 60 * 60 * 24);
        System.out.println(t3);

    }

}