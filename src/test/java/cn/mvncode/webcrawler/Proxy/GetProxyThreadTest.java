package cn.mvncode.webcrawler.Proxy;

import cn.mvncode.webcrawler.Utils.DateUtil;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Pavilion on 2017/3/25.
 */
public class GetProxyThreadTest {
    @Test
    public void launchProxyPool () throws Exception {
        System.out.println("startTime:" + DateUtil.timeNow());
        GetProxyThread thread = new GetProxyThread();
        while (true) {
            if (thread.isFlag()) {
                Proxy proxy = thread.getCurrentProxy();
                if (proxy == null) {
                    System.out.println("null" + DateUtil.timeNow());
                    continue;
                } else {
                    System.out.println(proxy.getHttpHost().getHostName() + DateUtil.timeNow());
                }
            }
            Thread.sleep(1000);
        }
    }

}