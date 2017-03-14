package cn.mvncode.webcrawler;

import cn.mvncode.webcrawler.Utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储下载的页面
 * Created by Pavilion on 2017/3/14.
 */
public class Page {

    private Request request;

    private int statusCode;

//    private boolean needCycleRetry;

//    private String rawText;

    private List<Request> targetRequest = new ArrayList<Request>();

    public Page () {
    }

    public List<Request> getTargetRequest () {
        return targetRequest;
    }

    /**
     * 添加待抓取url
     * 未完成
     * @param requests
     */
    public void addTargetRequest (List<String> requests) {
        for (String s : requests) {
            if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
                continue;
            }

        }
    }


}
