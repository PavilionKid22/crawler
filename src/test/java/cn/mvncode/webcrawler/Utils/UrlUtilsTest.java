package cn.mvncode.webcrawler.Utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Pavilion on 2017/3/18.
 */
public class UrlUtilsTest {

    @Test
    public void getCharset () throws Exception {

    }

    @Test
    public void removeProtocol () throws Exception {

    }

    @Test
    public void getDomain () throws Exception {

    }

    @Test
    public void removePort () throws Exception {

    }

    @Test
    public void getHost () throws Exception {
        System.out.println(UrlUtils.getHost("host:"+"http://blog.csdn.net/guolin6315/article/details/7296955"));
        System.out.println(UrlUtils.getDomain("domain:"+"http://blog.csdn.net/guolin6315/article/details/7296955"));
        System.out.println(UrlUtils.checkIP("119.116.74.22"));
        String a = "1";
        System.out.println(Integer.parseInt(a));
    }


}