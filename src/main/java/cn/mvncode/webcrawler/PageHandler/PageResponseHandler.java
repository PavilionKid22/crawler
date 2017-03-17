package cn.mvncode.webcrawler.PageHandler;

import cn.mvncode.webcrawler.Page;
import cn.mvncode.webcrawler.ResultItem;
import cn.mvncode.webcrawler.Utils.UrlUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * 网页解析
 * Created by Pavilion on 2017/3/16.
 */
public class PageResponseHandler implements ResponseHandler<ResultItem> {

    private Page page;

    public PageResponseHandler (Page page) {
        this.page = page;
    }


    @Override
    public ResultItem handleResponse (HttpResponse httpResponse) throws ClientProtocolException, IOException {

        ResultItem resultItem = new ResultItem();

        StatusLine statusLine = httpResponse.getStatusLine();
        HttpEntity httpEntity = httpResponse.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(httpEntity);//消费掉实体
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }

        if (httpEntity == null) {
            return null;
        }

        // 利用自带的EntityUtils把当前HttpResponse中的HttpEntity转化成HTML代码
        String html = EntityUtils.toString(httpEntity, "utf-8");

        Document document = Jsoup.parse(html);

        Elements links = document.getElementsByTag("a");//解析链接
        for (int i = 0; i < links.size(); i++) {
            Element link = links.get(i);
            if (StringUtils.isBlank(link.toString()) || link.toString().equals("#")) {
                continue;
            }
            String requestUrl = UrlUtils.canonocalizeUrl(link.toString(), page.getRequest().getUrl());
            resultItem.put("link", requestUrl);
        }

        Elements paragraphs = document.getElementsByTag("p");//解析段落
        StringBuffer plainText = new StringBuffer(html.length() / 2);
        for (int i = 0; i < paragraphs.size(); i++) {
            Element paragraph = paragraphs.get(i);
            plainText.append(paragraph.text()).append("\n");
        }
//        page.setRawText(plainText.toString());
        resultItem.put("paragraph", plainText);

        return resultItem;
    }
}
