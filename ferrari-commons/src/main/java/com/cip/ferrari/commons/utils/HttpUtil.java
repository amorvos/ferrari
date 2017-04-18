package com.cip.ferrari.commons.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http util to send data
 * 
 * @author xuxueli
 * @version 2015-11-28 15:30:59
 */
public class HttpUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    public static final String status = "status";

    public static final String msg = "msg";

    public static final String SUCCESS = "SUCCESS";

    public static final String FAIL = "FAIL";

    public static String[] post(String reqURL, Map<String, String> params) {
        String responseMsg = null;
        String exceptionMsg = null;

        // do post
        HttpPost httpPost = null;
        CloseableHttpClient httpClient = null;
        try {
            httpPost = new HttpPost(reqURL);
            httpPost.addHeader("Accept-Encoding", "gzip");
            httpClient = HttpClients.custom().addInterceptorFirst(new HttpResponseInterceptor() {

                @Override
                public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
                    // gzip解压
                    HttpEntity entity = response.getEntity();
                    Header ceheader = entity.getContentEncoding();
                    if (ceheader != null) {
                        for (HeaderElement e : ceheader.getElements()) {
                            if ("gzip".equalsIgnoreCase(e.getName())) {
                                HttpEntity dentity = new GzipDecompressingEntity(response.getEntity());
                                response.setEntity(dentity);
                                // logger.warn("receive response with gzip.");
                                return;
                            }

                        }
                    }
                }
            }).build();

            if (params != null && !params.isEmpty()) {
                List<NameValuePair> formParams = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
            }
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
            httpPost.setConfig(requestConfig);
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                if (null != entity) {
                    responseMsg = EntityUtils.toString(entity, "UTF-8");
                    EntityUtils.consume(entity);
                }
            } else {
                exceptionMsg = "http请求返回错误,code:" + response.getStatusLine().getStatusCode() + ",reason:"
                        + response.getStatusLine().getReasonPhrase();
            }
        } catch (Exception e) {
            LOGGER.error("send http post exception,requrl:" + reqURL + ",params:" + params, e);
            StringWriter out = new StringWriter();
            e.printStackTrace(new PrintWriter(out));
            exceptionMsg = out.toString();
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        String[] result = new String[2];
        result[0] = responseMsg;
        result[1] = exceptionMsg;
        return result;
    }
}
